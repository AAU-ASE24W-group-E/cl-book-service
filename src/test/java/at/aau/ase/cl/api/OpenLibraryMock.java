package at.aau.ase.cl.api;

import at.aau.ase.cl.client.openlibrary.OpenLibraryClient;
import at.aau.ase.cl.client.openlibrary.model.Author;
import at.aau.ase.cl.client.openlibrary.model.Book;
import at.aau.ase.cl.client.openlibrary.model.Work;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.io.IOException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ApplicationScoped
public class OpenLibraryMock {

    @Inject
    ObjectMapper objectMapper;

    Book bookMock;
    Work workMock;
    Author authorMock;

    public void setupClientMock(OpenLibraryClient openLibraryClientMock) throws Exception {
        setupClientMock(openLibraryClientMock, null);
    }

    public void setupClientMock(OpenLibraryClient openLibraryClientMock,
                                Set<String> knownIsbns) throws IOException {
        // execute OpenLibraryLearningTest to update mock data from the real API
        bookMock = readJson(Book.class);
        workMock = readJson(Work.class);
        authorMock = readJson(Author.class);

        doAnswer(i -> {
            String isbn = i.getArgument(0);
            if (knownIsbns == null || knownIsbns.contains(isbn)) {
                return bookMock;
            } else {
                throw new ClientWebApplicationException(Response
                        .status(404)
                        .type(MediaType.TEXT_HTML_TYPE)
                        .entity("""
                                <!DOCTYPE html>
                                <html xmlns="http://www.w3.org/1999/xhtml" lang="en">
                                <head>
                                     <title>/isbn/%s.json is not found | Open Library</title>
                                </head>
                                <body>
                                    <h1>Not Found</h1>
                                </body>
                                </html>
                                """.formatted(isbn))
                        .build());
            }
        }).when(openLibraryClientMock).getBookByIsbn(anyString());
        doReturn(workMock).when(openLibraryClientMock).getWorkById(anyString());
        doReturn(authorMock).when(openLibraryClientMock).getAuthorById(anyString());
    }

    <T> T readJson(Class<T> type) throws IOException {
        var name = "/mockdata/openlibrary/%s.json".formatted(type.getSimpleName().toLowerCase());
        var url = OpenLibraryMock.class.getResource(name);
        return objectMapper.readValue(url, type);
    }
}
