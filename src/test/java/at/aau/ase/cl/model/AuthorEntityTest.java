package at.aau.ase.cl.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorEntityTest {

    AuthorEntity author;

    @BeforeEach
    void setUp() {
        author = new AuthorEntity();
        author.name = "John Ronald Reuel Tolkien";
    }

    @Test
    void computeKeyShouldReduceForenameToInitials() {
        var key = author.computeKey();
        assertEquals("J.R.R.TOLKIEN", key);
    }

    @Test
    void computeKeyShouldDetectSurnameIfOnlyOneName() {
        author.name = "Tolkien";
        var key = author.computeKey();
        assertEquals("TOLKIEN", key);
    }

    @Test
    void computeKeyShouldDetectSurnameProvidedFirst() {
        author.name = "Tolkien, John Ronald Reuel";
        var key = author.computeKey();
        assertEquals("J.R.R.TOLKIEN", key);
    }

    @Test
    void isNewShouldReturnTrueIfIdIsNull() {
        author.id = null;
        assertTrue(author.isNew());
    }

    @Test
    void isNewShouldReturnFalseIfIdIsNotNull() {
        author.id = java.util.UUID.randomUUID();
        assertFalse(author.isNew());
    }

}