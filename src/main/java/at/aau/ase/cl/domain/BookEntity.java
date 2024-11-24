package at.aau.ase.cl.domain;

import at.aau.ase.cl.api.model.BookFormat;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import java.util.List;
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
    public String languages;

    @Embedded
    public ISBN isbn;

    public String publisher;

    public Integer publishYear;

    public String coverId;

    public String edition;

    @ManyToMany
    @JoinTable(name = "book_authoring",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id"))
    public List<AuthorEntity> authors;
}
