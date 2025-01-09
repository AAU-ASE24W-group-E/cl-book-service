package at.aau.ase.cl.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "book_owner", indexes = {
        @Index(name = "idx_book_owner_location", columnList = "location")
})
public class BookOwnerEntity extends PanacheEntityBase {
    @Id
    public UUID id;

    public String name;

    @Embedded
    public GeoLocation location;
}
