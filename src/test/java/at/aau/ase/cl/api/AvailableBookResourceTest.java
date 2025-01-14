package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.api.model.BookOwner;
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
class AvailableBookResourceTest {
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
    void findAvailableBooksWithDetailedParams() {
        var book = createTestBook();
        var ownerId = createTestOwner("Owner", 46.615691, 14.265178);
        createBookOwnership(ownerId, book.id());
        updateBookOwnership(ownerId, book.id(), true, false, false, BookStatus.AVAILABLE);

        given().queryParam("location", "46.623962,14.307691")
                .queryParam("distance", 10)
                .queryParam("title", "Lord")
                .queryParam("author", "Tolkien")
                .queryParam("isbn", TEST_ISBN)
                .queryParam("lendable", true)
                .get("/available-book")
                .then()
                .log().body(true)
                .statusCode(200)
                .body("hasMore", equalTo(false))
                .body("results[0].book.id", equalTo(book.id().toString()))
                .body("results[0].book.isbn", equalTo(TEST_ISBN))
                .body("results[0].owner.id", equalTo(ownerId.toString()))
                .body("results[0].owner.name", equalTo("Owner"))
                .body("results[0].owner.latitude", equalTo(46.615691f))
                .body("results[0].owner.longitude", equalTo(14.265178f))
                .body("results[0].lendable", equalTo(true))
                .body("results[0].giftable", equalTo(false))
                .body("results[0].exchangeable", equalTo(false));
    }

    @Test
    void findAvailableBooksWithQuickSearch() {
        var book = createTestBook();
        var ownerId = createTestOwner("Owner", 46.615691, -14.265178);
        createBookOwnership(ownerId, book.id());
        updateBookOwnership(ownerId, book.id(), true, false, false, BookStatus.AVAILABLE);

        given().queryParam("location", "46.623962,-14.307691")
                .queryParam("distance", 10)
                .queryParam("quickSearch", "Tolkien Lord of the Rings " + TEST_ISBN)
                .queryParam("lendable", true)
                .get("/available-book")
                .then()
                .log().body(true)
                .statusCode(200)
                .body("hasMore", equalTo(false))
                .body("results[0].book.id", equalTo(book.id().toString()))
                .body("results[0].book.isbn", equalTo(TEST_ISBN))
                .body("results[0].owner.id", equalTo(ownerId.toString()))
                .body("results[0].owner.name", equalTo("Owner"))
                .body("results[0].owner.latitude", equalTo(46.615691f))
                .body("results[0].owner.longitude", equalTo(-14.265178f))
                .body("results[0].lendable", equalTo(true))
                .body("results[0].giftable", equalTo(false))
                .body("results[0].exchangeable", equalTo(false));
    }

    Book createTestBook() {
        return given()
                .put("/book/isbn/{isbn}", TEST_ISBN)
                .then()
                .statusCode(200)
                .extract().as(Book.class);
    }

    UUID createTestOwner(String name, Double latitude, Double longitude) {
        UUID ownerId = UUID.randomUUID();
        given().contentType(ContentType.JSON)
                .body(new BookOwner(ownerId, name, latitude, longitude))
                .put("/book-owner/{ownerId}", ownerId)
                .then()
                .statusCode(204);
        return ownerId;
    }

    OwnBook createBookOwnership(UUID ownerId, UUID bookId) {
        return given().contentType(ContentType.JSON)
                .body(new OwnBook(ownerId, bookId, false, false, false, BookStatus.UNAVAILABLE))
                .post("/book-owner/{ownerId}/book/{bookId}", ownerId, bookId)
                .then()
                .statusCode(200)
                .extract().as(OwnBook.class);
    }

    void updateBookOwnership(UUID ownerId, UUID bookId, boolean lendable, boolean exchangeable, boolean giftable, BookStatus status) {
        given().contentType(ContentType.JSON)
                .body(new OwnBook(ownerId, bookId, lendable, giftable, exchangeable, status))
                .put("/book-owner/{ownerId}/book/{bookId}", ownerId, bookId)
                .then()
                .statusCode(204);
    }

}