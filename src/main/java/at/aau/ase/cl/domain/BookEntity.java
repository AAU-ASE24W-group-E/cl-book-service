package at.aau.ase.cl.domain;

import at.aau.ase.cl.api.model.BookFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.logging.Log;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "book")
public class BookEntity extends PanacheEntityBase {
    @Id
    public UUID id;

    @Column(nullable = false)
    public String title;

    @Enumerated(EnumType.STRING)
    public BookFormat format;

    /** comma separated list of alpha-2 language codes, e.g. 'en,de' */
    @Embedded
    public Languages languages;

    @Embedded
    public ISBN isbn;

    public String publisher;

    public Integer publishYear;

    public String coverId;

    public String edition;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_authoring",
            joinColumns = @JoinColumn(name = "book_id",
                    foreignKey = @ForeignKey(name = "fk_book_authoring_book_id")),
            inverseJoinColumns = @JoinColumn(name = "author_id",
                    foreignKey = @ForeignKey(name = "fk_book_authoring_author_id")))
    public List<AuthorEntity> authors;

    public static BookEntity findByIsbn(ISBN isbn) {
        BookEntity book = find("isbn.number", isbn.toLong()).firstResult();
        Log.debugf("findBook by isbn %s -> %s", isbn, book);
        return book;
    }

    public static List<BookEntity> findByTitleAndAuthor(String title, String author, int maxResults) {
        var titleCriterion = new WildcardCriterion(title);
        var authorCriterion = new WildcardCriterion(author);
        List<String> criteria = new LinkedList<>();
        Map<String, Object> params = new HashMap<>();
        if (titleCriterion.isPresent()) {
            criteria.add("title ilike :title");
            params.put("title", titleCriterion.prepare());
        }
        if (authorCriterion.isPresent()) {
            criteria.add("element(authors).name ilike :author");
            params.put("author", authorCriterion.prepare());
        }
        if (criteria.isEmpty()) {
            return List.of();
        }
        String ql = String.join(" and ", criteria);
        var query = find(ql, Sort.by("title"), params);
        query = query.range(0, maxResults);
        return query.list();
    }

}
