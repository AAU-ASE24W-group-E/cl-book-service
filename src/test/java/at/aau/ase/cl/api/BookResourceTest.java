package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.api.model.BookFormat;
import at.aau.ase.cl.client.openlibrary.OpenLibraryClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class BookResourceTest {
    @InjectMock
    @RestClient
    OpenLibraryClient openLibraryClientMock;

    @Inject
    ObjectMapper objectMapper;

    at.aau.ase.cl.client.openlibrary.model.Book olBookMock;
    at.aau.ase.cl.client.openlibrary.model.Work olWorkMock;
    at.aau.ase.cl.client.openlibrary.model.Author olAuthorMock;

    @BeforeEach
    void setUp() throws Exception {
        // execute OpenLibraryLearningTest to update mock data from the real API
        olBookMock = readJson(at.aau.ase.cl.client.openlibrary.model.Book.class);
        olWorkMock = readJson(at.aau.ase.cl.client.openlibrary.model.Work.class);
        olAuthorMock = readJson(at.aau.ase.cl.client.openlibrary.model.Author.class);

        doReturn(olBookMock).when(openLibraryClientMock).getBookByIsbn(anyString());
        doReturn(olWorkMock).when(openLibraryClientMock).getWorkById(anyString());
        doReturn(olAuthorMock).when(openLibraryClientMock).getAuthorById(anyString());
    }

    <T> T readJson(Class<T> type) throws IOException {
        var name = "/mockdata/openlibrary/%s.json".formatted(type.getSimpleName().toLowerCase());
        var url = this.getClass().getResource(name);
        return objectMapper.readValue(url, type);
    }

    @Test
    void createBook() {
        given()
                .contentType(ContentType.JSON)
                .body(new Book(null, "The Title", "978-0-618-34399-7", List.of("en"), BookFormat.HARDCOVER,
                        "Publisher inc", 2024, "OL123", "edition",
                        List.of("A. U. Thor")))
                .post("/book")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Title"))
                .body("authors[0]", equalTo("A. U. Thor"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo("978-0-618-34399-7"))
                .body("publishYear", equalTo(2024))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Publisher inc"))
                .body("coverId", equalTo("OL123"))
                .body("id", notNullValue());
    }

    @Test
    void createMultipleBooksOfSameAuthor() {
        given().contentType(ContentType.JSON)
                .body(new Book(null, "The Title", "978-0-618-34399-7", List.of("en"), BookFormat.HARDCOVER,
                        "Publisher inc", 2024, "OL123", null, List.of("A. U. Thor")))
                .post("/book")
                .then()
                .statusCode(200)
                .body("title", equalTo("The Title"))
                .body("authors[0]", equalTo("A. U. Thor"));

        given().contentType(ContentType.JSON)
                .body(new Book(null, "Der Titel", "978-0-198-52663-6", List.of("de-AT"), BookFormat.PAPERBACK,
                        "Publisher inc", 2024, "OL125", null, List.of("Arthur Ulrich Thor")))
                .post("/book")
                .then()
                .statusCode(200)
                .body("title", equalTo("Der Titel"))
                // the first author name is used
                .body("authors[0]", equalTo("A. U. Thor"));
    }

    @Test
    void importBookByIsbn() {
        given().pathParam("isbn", "978-0-618-34399-7")
                .put("/isbn/{isbn}")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Lord of the Rings"))
                .body("authors[0]", equalTo("J.R.R. Tolkien"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo("978-0-618-34399-7"))
                .body("publishYear", equalTo(2003))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Houghton Mifflin Company"))
                .body("coverId", equalTo("14627570"))
                .body("edition", equalTo("Movie tie-in edition (1)"))
                .body("id", notNullValue());
    }

    @Test
    void findByIsbnShouldReturnImportedBook() {
        String isbn = "978-0-618-34399-7";

        given().pathParam("isbn", isbn)
                .put("/isbn/{isbn}")
                .then()
                .statusCode(200);

        given().pathParam("isbn", isbn)
                .get("/isbn/{isbn}")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Lord of the Rings"))
                .body("authors[0]", equalTo("J.R.R. Tolkien"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo("978-0-618-34399-7"))
                .body("publishYear", equalTo(2003))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Houghton Mifflin Company"))
                .body("coverId", equalTo("14627570"))
                .body("edition", equalTo("Movie tie-in edition (1)"));
    }

    @Test
    void findByIsbnShouldReturn400ForInvalidIsbn() {
        given().pathParam("isbn", "978-0-618-34399-9")
                .get("/isbn/{isbn}")
                .then()
                .statusCode(400)
                .body("type", equalTo("IllegalArgumentException"));
    }

    @Test
    void findByIsbnShouldReturn404ForUnknownIsbn() {
        given().pathParam("isbn", "9780385472579")
                .get("/isbn/{isbn}")
                .then()
                .statusCode(404);
    }
}