package at.aau.ase.cl.domain;

import at.aau.ase.cl.api.model.BookStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
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

        String ql = """
            select a, b, o, ST_Distance(o.location, :location) as distance
            from BookOwnershipEntity a
            join a.book b
            join a.owner o
            where ST_Distance(o.location, :location) <= :distance
            """;
        Map<String, Object> params = new HashMap<>();
        params.put("location", criteria.location.point);
        params.put("distance", criteria.distance * 1000.0); // km to meters

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
            ql += statuses.stream().map(s -> "a." + s)
                    .collect(Collectors.joining(" or ", " and (", ")"));
        }
        var titleCriterion = new WildcardCriterion(criteria.title);
        if (titleCriterion.isPresent()) {
            ql += " and b.title ilike :title";
            params.put("title", titleCriterion.prepare());
        }
        var authorCriterion = new WildcardCriterion(criteria.author);
        if (authorCriterion.isPresent()) {
            ql += " and exists (select u.id from AuthorEntity u join b.authors where u.name ilike :author)";
            params.put("author", authorCriterion.prepare());
        }
        if (criteria.isbn != null) {
            var isbn = ISBN.fromString(criteria.isbn);
            ql += " and b.isbn.number = :isbn";
            params.put("isbn", isbn.toLong());
        }

        // quick search terms: and (title like term1 or title like term2 or author like term1 or ...)
        if (!criteria.quickSearchTerms.isEmpty()) {
            int n = criteria.quickSearchTerms.size();
            String titlePart = null;
            String authorPart = null;
            String isbnPart = null;

            if (titleCriterion.isBlank()) {
                List<String> titleTerms = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    var term = new WildcardCriterion(criteria.quickSearchTerms.get(i));
                    String paramName = "title" + i;
                    params.put(paramName, term.prepare());
                    titleTerms.add("b.title ilike :" + paramName);
                }
                titlePart = titleTerms.stream().collect(Collectors.joining(" or ", "(", ")"));
            }
            if (authorCriterion.isBlank()) {
                List<String> authorTerms = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    var term = new WildcardCriterion(criteria.quickSearchTerms.get(i));
                    String paramName = "author" + i;
                    params.put(paramName, term.prepare());
                    authorTerms.add("u.name ilike :" + paramName);
                }
                authorPart = "exists (select u.id from AuthorEntity u join b.authors where "
                    + authorTerms.stream().collect(Collectors.joining(" or ", "", ")"));
            }
            if (criteria.isbn == null) {
                List<String> isbnTerms = new ArrayList<>(n);
                for (int i = 0; i < n; i++) {
                    try {
                        var isbn = ISBN.fromString(criteria.quickSearchTerms.get(i));
                        String paramName = "isbn" + i;
                        params.put(paramName, isbn.toLong());
                        isbnTerms.add("b.isbn.number = :" + paramName);
                    } catch (IllegalArgumentException e) {
                        // TODO refactor to avoid misusing exception in logic
                    }
                }
                isbnPart = isbnTerms.stream().collect(Collectors.joining(" or ", "(", ")"));
            }

            ql += Stream.of(titlePart, authorPart, isbnPart)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" or ", " and (", ")"));
        }

        ql += " order by ST_Distance(o.location, :location)";

        PanacheQuery<BookOwnershipEntity> query = find(ql, params);
        return query.range(criteria.offset, criteria.limit + 1)
                .project(AvailableBookProjection.class)
                .list();
    }
}
