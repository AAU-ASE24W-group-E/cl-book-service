package at.aau.ase.cl.domain;

import at.aau.ase.cl.api.model.BookSortingProperty;
import at.aau.ase.cl.api.model.BookStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "book_ownership")
public class BookOwnershipEntity extends PanacheEntityBase {
    // composite primary key: (owner_id, book_id)

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", foreignKey = @ForeignKey(name = "fk_book_ownership_owner_id"))
    public BookOwnerEntity owner;

    @Id
    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "fk_book_ownership_book_id"))
    public BookEntity book;

    @Transient
    public UUID ownerId;

    @Transient
    public UUID bookId;

    public boolean lendable;

    public boolean giftable;

    public boolean exchangeable;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    public BookStatus status;

    public static BookOwnershipEntity findByOwnerAndBook(UUID ownerId, UUID bookId) {
        var query = find("owner.id = ?1 and book.id = ?2", ownerId, bookId);
        return query.firstResult();
    }

    public static List<AvailableBookProjection> findAvailableBooks(AvailableBooksSearchCriteria criteria) {
        StringBuilder ql = new StringBuilder("""
            select a, b, o, ST_Distance(o.location, :location) as distance
            from BookOwnershipEntity a
            join a.book b
            join a.owner o
            where ST_Distance(o.location, :location) <= :distance
            """);
        Map<String, Object> params = new HashMap<>();
        params.put("location", criteria.location.point);
        params.put("distance", criteria.distance * 1000.0); // km to meters

        addStatusFlagsCriteria(ql, criteria);

        var titleCriterion = new WildcardCriterion(criteria.title);
        if (titleCriterion.isPresent()) {
            ql.append(" and b.title ilike :title");
            params.put("title", titleCriterion.prepare());
        }

        var authorCriterion = new WildcardCriterion(criteria.author);
        if (authorCriterion.isPresent()) {
            ql.append(" and exists (select u.id from AuthorEntity u join b.authors where u.name ilike :author)");
            params.put("author", authorCriterion.prepare());
        }

        if (criteria.isbn != null) {
            var isbn = ISBN.fromString(criteria.isbn);
            ql.append(" and b.isbn.number = :isbn");
            params.put("isbn", isbn.toLong());
        }

        addQuickSearchCriteria(ql, params, criteria,
                titleCriterion.isBlank(), authorCriterion.isBlank(), criteria.isbn == null);

        ql.append(" order by ST_Distance(o.location, :location)");

        PanacheQuery<BookOwnershipEntity> query = find(ql.toString(), params);
        return query.range(criteria.offset, criteria.limit + 1)
                .project(AvailableBookProjection.class)
                .list();
    }

    static void addStatusFlagsCriteria(StringBuilder ql, AvailableBooksSearchCriteria criteria) {
        if (criteria.lendable || criteria.exchangeable || criteria.giftable) {
            List<String> statuses = new LinkedList<>();
            if (criteria.lendable) {
                statuses.add("lendable");
            }
            if (criteria.exchangeable) {
                statuses.add("exchangeable");
            }
            if (criteria.giftable) {
                statuses.add("giftable");
            }
            ql.append(statuses.stream().map(s -> "a." + s)
                    .collect(Collectors.joining(" or ", " and (", ")")));
        }
    }

    static void addQuickSearchCriteria(StringBuilder ql, Map<String, Object> params,
                                       AvailableBooksSearchCriteria criteria,
                                       boolean inTitle, boolean inAuthorName, boolean inIsbn) {
        // quick search terms: and (title like term1 or title like term2 or author like term1 or ...)
        if (!criteria.quickSearchTerms.isEmpty()) {
            String titlePart = null;
            String authorPart = null;
            String isbnPart = null;

            if (inTitle) {
                titlePart = addTermsDisjunction("(", ")", params, criteria, "title",
                        p -> "b.title ilike :" + p,
                        v -> new WildcardCriterion(v).prepare());
            }
            if (inAuthorName) {
                authorPart = addTermsDisjunction(
                        "exists (select u.id from AuthorEntity u join b.authors where ", ")",
                        params, criteria, "author",
                        p -> "u.name ilike :" + p,
                        v -> new WildcardCriterion(v).prepare());
            }
            if (inIsbn) {
                isbnPart = addTermsDisjunction("(", ")", params, criteria, "isbn",
                        p -> "b.isbn.number = :" + p,
                        v -> ISBN.isValidIsbn(v) ? ISBN.fromString(v).toLong() : null);
            }

            ql.append(Stream.of(titlePart, authorPart, isbnPart)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" or ", " and (", ")")));
        }
    }

    private static String addTermsDisjunction(String qlPrefix, String qlSuffix,
                                              Map<String, Object> params,
                                              AvailableBooksSearchCriteria criteria,
                                              String paramPrefix,
                                              UnaryOperator<String> qlTermGenerator,
                                              Function<String, Object> termValueTransformer) {
        int n = criteria.quickSearchTerms.size();
        List<String> atoms = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            var term = criteria.quickSearchTerms.get(i);
            var value = termValueTransformer.apply(term);
            if (value != null) {
                var paramName = paramPrefix + i;
                params.put(paramName, value);
                var ql = qlTermGenerator.apply(paramName);
                atoms.add(ql);
            }
        }
        if (atoms.isEmpty()) {
            return null;
        }
        return atoms.stream().collect(Collectors.joining(" or ", qlPrefix, qlSuffix));
    }

    public static List<BookOwnershipEntity> findByOwner(UUID ownerId, BookSortingProperty sortBy, Boolean descending) {
        Sort.Direction dir = descending != null && descending ? Sort.Direction.Descending : Sort.Direction.Ascending;
        Sort sort = switch (sortBy) {
            case null -> Sort.by("book.title", dir);
            case TITLE -> Sort.by("book.title", dir);
            case YEAR -> Sort.by("book.publishYear", dir);
            case STATUS -> Sort.by("status", dir);
            default -> {
                Log.debugf("Unknown sorting property %s, falling back to default", sortBy);
                yield Sort.by("book.id", dir);
            }
        };
        return find("from BookOwnershipEntity o join fetch o.book where o.owner.id=?1", sort, ownerId).list();
    }
}
