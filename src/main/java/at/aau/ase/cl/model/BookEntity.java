package at.aau.ase.cl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "book")
public class BookEntity extends PanacheEntityBase {
    @Id
    public UUID id;

    @Column(name = "owner_id", nullable = false)
    public UUID ownerId;

    @Column(name = "title", nullable = false)
    public String title;

    @Column(name = "author")
    public String author;

    public List<BookEntity> findByOwnerId(UUID ownerId) {

        return find("ownerId", Sort.by("title"), ownerId).list();
    }
}
