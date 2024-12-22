package at.aau.ase.cl.domain;

import at.aau.ase.cl.api.model.BookStatus;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
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

import java.util.UUID;

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

    public boolean exchangable;

    @Enumerated(EnumType.STRING)
    @Column(length = 16)
    public BookStatus status;

    public static BookOwnershipEntity findByOwnerAndBook(UUID ownerId, UUID bookId) {
        var query = find("owner.id = ?1 and book.id = ?2", ownerId, bookId);
        return query.firstResult();
    }
}
