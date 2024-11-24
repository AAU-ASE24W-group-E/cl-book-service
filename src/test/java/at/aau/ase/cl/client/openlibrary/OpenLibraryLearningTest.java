package at.aau.ase.cl.client.openlibrary;

import io.quarkus.logging.Log;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// see https://openlibrary.org/developers/api
@Disabled("Disabled for CI because it calls external API")
@QuarkusTest
class OpenLibraryLearningTest {
    @Inject
    @RestClient
    OpenLibraryClient openLibraryClient;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getBookByIsbn() {
        var book = openLibraryClient.getBookByIsbn("978-0-618-34399-7");
        assertNotNull(book);
        Log.infof("Book: %s", book);
    }

    @Test
    void getBookById() {
        var book = openLibraryClient.getBookById("OL51711484M");
        assertNotNull(book);
        Log.infof("Book: %s", book);
    }

    @Test
    void getWorkById() {
        var work = openLibraryClient.getWorkById("OL27448W");
        assertNotNull(work);
        Log.infof("Work: %s", work);
    }

    @Test
    void getAuthorById() {
        var author = openLibraryClient.getAuthorById("OL26320A");
        assertNotNull(author);
        Log.infof("Author: %s", author);
    }
}