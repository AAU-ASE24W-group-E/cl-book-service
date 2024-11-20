package at.aau.ase.cl.mapper;

import at.aau.ase.cl.model.AuthorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookMapperTest {

    BookMapper mapper = BookMapper.INSTANCE;

    String name;
    AuthorEntity author;

    @BeforeEach
    void setUp() {
        name = "John Ronald Reuel Tolkien";
        author = new AuthorEntity();
        author.name = name;
    }

    @Test
    void mapAuthorNameShouldReturnName() {
        String n = mapper.mapAuthorName(author);
        assertEquals(author.name, n);
    }

    @Test
    void mapAuthorNameShouldReturnNullIfAuthorIsNull() {
        author = null;
        String n = mapper.mapAuthorName(author);
        assertNull(n);
    }

    @Test
    void mapAuthorShouldReturnAuthor() {
        AuthorEntity a = mapper.mapAuthor(name);
        assertNotNull(a);
        assertEquals(name, a.name);
    }

    @Test
    void mapAuthorShouldReturnNullIfNameIsNull() {
        name = null;
        AuthorEntity a = mapper.mapAuthor(name);
        assertNull(a);
    }

    @Test
    void mapAuthorShouldReturnNullIfNameIsBlank() {
        name = " ";
        AuthorEntity a = mapper.mapAuthor(name);
        assertNull(a);
    }
}