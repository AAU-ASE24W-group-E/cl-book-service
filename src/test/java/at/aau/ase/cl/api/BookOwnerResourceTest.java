package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.api.model.BookStatus;
import at.aau.ase.cl.api.model.OwnBook;
import at.aau.ase.cl.client.openlibrary.OpenLibraryClient;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class BookOwnerResourceTest {
    static final String PATH_BOOK_OWNER_BOOK_ID = "/book-owner/{ownerId}/book/{bookId}";
    static final String PATH_BOOK_OWNER_BOOK = "/book-owner/{ownerId}/book";

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

    Book createTestBook() {
        return given()
                .put("/book/isbn/{isbn}", TEST_ISBN)
                .then()
                .statusCode(200)
                .extract().as(Book.class);
    }

    @Test
    void createBookOwnerShouldAddNewBookForNewUser() {
        UUID ownerId = UUID.randomUUID();
        var book = createTestBook();
        given().contentType(ContentType.JSON)
                .post(PATH_BOOK_OWNER_BOOK_ID, ownerId, book.id())
                .then()
                .statusCode(200)
                .body("ownerId", equalTo(ownerId.toString()))
                .body("book.id", equalTo(book.id().toString()))
                .body("book.isbn", equalTo(book.isbn()))
                .body("book.title", equalTo(book.title()))
                .body("lendable", equalTo(false))
                .body("giftable", equalTo(false))
                .body("exchangable", equalTo(false))
                .body("status", equalTo("UNAVAILABLE"));
    }

    @Test
    void updateBookOwnerShouldUpdateExistingBook() {
        UUID ownerId = UUID.randomUUID();
        UUID bookId = createTestBook().id();

        given().contentType(ContentType.JSON)
                .post(PATH_BOOK_OWNER_BOOK_ID, ownerId, bookId)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .body(new OwnBook(ownerId, null, true, true, true, BookStatus.AVAILABLE))
                .put(PATH_BOOK_OWNER_BOOK_ID, ownerId, bookId)
                .then()
                .statusCode(204);
    }

    @Test
    void createBookOwnerShouldReturn404IfBookNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        given().contentType(ContentType.JSON)
                .post(PATH_BOOK_OWNER_BOOK_ID, ownerId, bookId)
                .then()
                .statusCode(404);
    }

    @Test
    void updateBookOwnerShouldReturn404IfOwnBookNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        given().contentType(ContentType.JSON)
                .body(new OwnBook(null, null, false, false, false, BookStatus.AVAILABLE))
                .put(PATH_BOOK_OWNER_BOOK_ID, ownerId, bookId)
                .then()
                .statusCode(404);
    }

    @Test
    void getOwnBooksShouldReturnAllBooksOfOwner() {
        UUID ownerId = UUID.randomUUID();
        var book = createTestBook();

        given().contentType(ContentType.JSON)
                .post(PATH_BOOK_OWNER_BOOK_ID, ownerId, book.id())
                .then()
                .statusCode(200);

        given().get(PATH_BOOK_OWNER_BOOK, ownerId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("[0].ownerId", equalTo(ownerId.toString()))
                .body("[0].book.id", equalTo(book.id().toString()))
                .body("[0].book.isbn", equalTo(book.isbn()))
                .body("[0].book.title", equalTo(book.title()))
                .body("[0].lendable", equalTo(false))
                .body("[0].giftable", equalTo(false))
                .body("[0].exchangable", equalTo(false))
                .body("[0].status", equalTo("UNAVAILABLE"));
    }

    @Test
    void getOwnBooksShouldReturnEmptyListIfOwnerHasNoBooks() {
        UUID ownerId = UUID.randomUUID();
        createTestBook();

        // book exists, but ownership is not set!

        given().get(PATH_BOOK_OWNER_BOOK, ownerId)
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));
    }
}