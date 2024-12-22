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
import static org.mockito.Mockito.*;

@QuarkusTest
class BookOwnerResourceTest {
    static final String PATH_BOOK_OWNER_BOOK = "/book-owner/{ownerId}/book/{bookId}";

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
    void updateBookOwnerShouldAddNewBookForNewUser() {
        UUID ownerId = UUID.randomUUID();
        UUID bookId = createTestBook().id();
        given().contentType(ContentType.JSON)
                .body(new OwnBook(UUID.randomUUID(), UUID.randomUUID(), false, false, false, BookStatus.AVAILABLE))
                .put(PATH_BOOK_OWNER_BOOK, ownerId, bookId)
                .then()
                .statusCode(204);
    }

    @Test
    void updateBookOwnerShouldUpdateExistingBook() {
        UUID ownerId = UUID.randomUUID();
        UUID bookId = createTestBook().id();

        given().contentType(ContentType.JSON)
                .body(new OwnBook(ownerId, bookId, false, false, false, BookStatus.AVAILABLE))
                .put(PATH_BOOK_OWNER_BOOK, ownerId, bookId)
                .then()
                .statusCode(204);

        given().contentType(ContentType.JSON)
                .body(new OwnBook(ownerId, bookId, true, true, true, BookStatus.AVAILABLE))
                .put(PATH_BOOK_OWNER_BOOK, ownerId, bookId)
                .then()
                .statusCode(204);
    }

    @Test
    void updateBookOwnerShouldReturn404IfBookNotFound() {
        UUID ownerId = UUID.randomUUID();
        UUID bookId = UUID.randomUUID();
        given().contentType(ContentType.JSON)
                .body(new OwnBook(ownerId, bookId, false, false, false, BookStatus.AVAILABLE))
                .put(PATH_BOOK_OWNER_BOOK, ownerId, bookId)
                .then()
                .statusCode(404);
    }
}