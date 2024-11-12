package at.aau.ase.cl.api;

import at.aau.ase.cl.api.model.Book;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

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
                .body(new Book(UUID.randomUUID(), "title", "author"))
                .post("/book")
                .then()
                .statusCode(200)
                .log().body(true)
                .body("title", equalTo("title"))
                .body("author", equalTo("author"))
                .body("ownerId", notNullValue())
                .body("id", notNullValue());
    }

    @Test
    void getBook() {
    }
}