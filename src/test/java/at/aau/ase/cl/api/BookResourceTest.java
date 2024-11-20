package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.api.model.BookFormat;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class BookResourceTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void createBook() {
        given()
                .contentType(ContentType.JSON)
                .body(new Book(null, "The Title", "978-0-618-34399-7", "en", BookFormat.HARDCOVER,
                        "Publisher inc", 2024, "OL123", List.of("A. U. Thor")))
                .post("/book")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Title"))
                .body("authors[0]", equalTo("A. U. Thor"))
                .body("language", equalTo("en"))
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
                .body(new Book(null, "The Title", "978-0-618-34399-7", "en", BookFormat.HARDCOVER,
                        "Publisher inc", 2024, "OL123", List.of("A. U. Thor")))
                .post("/book")
                .then()
                .statusCode(200)
                .body("title", equalTo("The Title"))
                .body("authors[0]", equalTo("A. U. Thor"));

        given().contentType(ContentType.JSON)
                .body(new Book(null, "Der Titel", "978-0-618-34399-8", "de-AT", BookFormat.PAPERBACK,
                        "Publisher inc", 2024, "OL125", List.of("Arthur Ulrich Thor")))
                .post("/book")
                .then()
                .statusCode(200)
                .body("title", equalTo("Der Titel"))
                // the first author name is used
                .body("authors[0]", equalTo("A. U. Thor"));
    }


}