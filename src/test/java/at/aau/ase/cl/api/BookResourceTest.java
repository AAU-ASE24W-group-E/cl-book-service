package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.api.model.BookFormat;
import at.aau.ase.cl.client.openlibrary.OpenLibraryClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class BookResourceTest {
    static final String PATH_BOOK = "/book";
    static final String PATH_BOOK_BY_ID = "/book/{id}";
    static final String PATH_BOOK_BY_ISBN = "/book/isbn/{isbn}";

    static final String TEST_ISBN = "978-0-618-34399-7";
    
    @InjectMock
    @RestClient
    OpenLibraryClient openLibraryClientMock;

    @Inject
    OpenLibraryMock openLibraryMock;

    @BeforeEach
    void setUp() throws Exception {
        openLibraryMock.setupClientMock(openLibraryClientMock);
    }

    @AfterEach
    void tearDown() {
        reset(openLibraryClientMock);
    }

    @Test
    void createBook() {
        given()
                .contentType(ContentType.JSON)
                .body(new Book(null, "The Title", TEST_ISBN, List.of("en"), BookFormat.HARDCOVER,
                        "Publisher inc", 2024, "OL123", "edition",
                        List.of("A. U. Thor")))
                .post(PATH_BOOK)
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Title"))
                .body("authors[0]", equalTo("A. U. Thor"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo(TEST_ISBN))
                .body("publishYear", equalTo(2024))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Publisher inc"))
                .body("coverId", equalTo("OL123"))
                .body("id", notNullValue());
    }

    @Test
    void createMultipleBooksOfSameAuthor() {
        given().contentType(ContentType.JSON)
                .body(new Book(null, "The Title", TEST_ISBN, List.of("en"), BookFormat.HARDCOVER,
                        "Publisher inc", 2024, "OL123", null, List.of("A. U. Thor")))
                .post(PATH_BOOK)
                .then()
                .statusCode(200)
                .body("title", equalTo("The Title"))
                .body("authors[0]", equalTo("A. U. Thor"));

        given().contentType(ContentType.JSON)
                .body(new Book(null, "Der Titel", "978-0-198-52663-6", List.of("de-AT"), BookFormat.PAPERBACK,
                        "Publisher inc", 2024, "OL125", null, List.of("Arthur Ulrich Thor")))
                .post(PATH_BOOK)
                .then()
                .statusCode(200)
                .body("title", equalTo("Der Titel"))
                // the first author name is used
                .body("authors[0]", equalTo("A. U. Thor"));
    }

    @Test
    void importBookByIsbn() {
        given().put(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Lord of the Rings"))
                .body("authors[0]", equalTo("J.R.R. Tolkien"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo(TEST_ISBN))
                .body("publishYear", equalTo(2003))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Houghton Mifflin Company"))
                .body("coverId", equalTo("14627570"))
                .body("edition", equalTo("Movie tie-in edition (1)"))
                .body("id", notNullValue());
    }

    @Test
    void findByIsbnShouldReturnImportedBook() {
        given().put(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200);

        given().get(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Lord of the Rings"))
                .body("authors[0]", equalTo("J.R.R. Tolkien"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo(TEST_ISBN))
                .body("publishYear", equalTo(2003))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Houghton Mifflin Company"))
                .body("coverId", equalTo("14627570"))
                .body("edition", equalTo("Movie tie-in edition (1)"));
    }

    @Test
    void findByIsbnShouldReturn400ForInvalidIsbn() {
        given().get(PATH_BOOK_BY_ISBN, "978-0-618-34399-9")
                .then()
                .statusCode(400)
                .body("type", equalTo("IllegalArgumentException"));
    }

    @Test
    void findByIsbnShouldReturn404ForUnknownIsbn() {
        given().get(PATH_BOOK_BY_ISBN, "9780385472579")
                .then()
                .statusCode(404);
    }

    @Test
    void getBookByIdShouldReturnImportedBook() {
        Book book = given().put(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200)
                .extract().body().as(Book.class);

        given().get(PATH_BOOK_BY_ID, book.id())
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("The Lord of the Rings"))
                .body("authors[0]", equalTo("J.R.R. Tolkien"))
                .body("languages[0]", equalTo("en"))
                .body("isbn", equalTo(TEST_ISBN))
                .body("publishYear", equalTo(2003))
                .body("format", equalTo("HARDCOVER"))
                .body("publisher", equalTo("Houghton Mifflin Company"))
                .body("coverId", equalTo("14627570"))
                .body("edition", equalTo("Movie tie-in edition (1)"));
    }

    @Test
    void getBookByIdShouldReturn404ForUnknownId() {
        given().pathParam("id", "00000000-0000-0000-0000-000000000000")
                .get(PATH_BOOK_BY_ID)
                .then()
                .statusCode(404);
    }

    @Test
    void getBookByIdShouldReturn404ForInvalidPathParam() {
        given().pathParam("id", "invalid-uuid")
                .get(PATH_BOOK_BY_ID)
                .then()
                .statusCode(404);
    }

    @Test
    void findBookByTitleAndAuthor() {
        given().put(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200);

        given().queryParam("title", "The Lord of the Rings")
                .queryParam("author", "J.R.R. Tolkien")
                .get(PATH_BOOK)
                .then()
                .log().all(true)
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].title", equalTo("The Lord of the Rings"))
                .body("[0].authors[0]", equalTo("J.R.R. Tolkien"));
    }

    @Test
    void findBookByTitleOnly() {
        given().put(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200);

        given().queryParam("title", "lord of the rings")
                .get(PATH_BOOK)
                .then()
                .log().all(true)
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].title", equalTo("The Lord of the Rings"));
    }

    @Test
    void findBookByAuthorOnly() {
        given().put(PATH_BOOK_BY_ISBN, TEST_ISBN)
                .then()
                .statusCode(200);

        given().queryParam("author", "Tolkien")
                .get(PATH_BOOK)
                .then()
                .log().all(true)
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].authors[0]", equalTo("J.R.R. Tolkien"));
    }

}