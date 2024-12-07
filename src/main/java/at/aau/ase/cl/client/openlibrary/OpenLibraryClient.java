package at.aau.ase.cl.client.openlibrary;

import at.aau.ase.cl.client.openlibrary.model.Author;
import at.aau.ase.cl.client.openlibrary.model.Book;
import at.aau.ase.cl.client.openlibrary.model.Work;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * OpenLibrary client
 * see <a href="https://openlibrary.org/swagger/docs">Swagger UI</a>
 * see <a href="https://openlibrary.org/dev/docs/api/books">Books API</a>
 */
@RegisterRestClient(configKey = "openlibrary")
public interface OpenLibraryClient {
    @GET
    @Path("isbn/{isbn}.json")
    Book getBookByIsbn(@PathParam("isbn") String isbn);

    @GET
    @Path("books/{id}.json")
    Book getBookById(@PathParam("id") String id);

    @GET
    @Path("works/{id}.json")
    Work getWorkById(@PathParam("id") String id);

    @GET
    @Path("authors/{id}.json")
    Author getAuthorById(@PathParam("id") String id);
}
