package at.aau.ase.cl.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "book_author", indexes = {
        @Index(name = "idx_book_author_key", columnList = "key", unique = true)
})
public class AuthorEntity extends PanacheEntityBase {
    @Id
    public UUID id;

    @Column(nullable = false)
    public String key;

    @Column(nullable = false)
    public String name;

    public static AuthorEntity findByKey(String key) {
        return find("key", key).firstResult();
    }

    /**
     * Computes the key for this author entity based on the name.
     * @return the computed key
     */
    public String computeKey() {
        // detect surname and forenames
        String surname;
        String forenames;
        if (name.contains(",")) {
            surname = name.substring(0, name.indexOf(","));
            forenames = name.substring(name.indexOf(",") + 1);
        } else {
            surname = name.substring(name.lastIndexOf(" ") + 1);
            forenames = name.substring(0, name.lastIndexOf(" "));
        }
        // replace forenames with initials
        String initials = Arrays.stream(forenames.trim().split("[ .]+"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + ".")
                .collect(Collectors.joining());
        key = initials + surname.trim().toUpperCase();
        return key;
    }

    public boolean isNew() {
        return id == null;
    }
}
