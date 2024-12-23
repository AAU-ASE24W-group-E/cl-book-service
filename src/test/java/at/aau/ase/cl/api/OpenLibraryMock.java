package at.aau.ase.cl.api;

import at.aau.ase.cl.client.openlibrary.OpenLibraryClient;
import at.aau.ase.cl.client.openlibrary.model.Author;
import at.aau.ase.cl.client.openlibrary.model.Book;
import at.aau.ase.cl.client.openlibrary.model.Work;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ApplicationScoped
public class OpenLibraryMock {

    @Inject
    ObjectMapper objectMapper;

    Book bookMock;
    Work workMock;
    Author authorMock;

    public void setupClientMock(OpenLibraryClient openLibraryClientMock) throws IOException {
        // execute OpenLibraryLearningTest to update mock data from the real API
        bookMock = readJson(Book.class);
        workMock = readJson(Work.class);
        authorMock = readJson(Author.class);

        doReturn(bookMock).when(openLibraryClientMock).getBookByIsbn(anyString());
        doReturn(workMock).when(openLibraryClientMock).getWorkById(anyString());
        doReturn(authorMock).when(openLibraryClientMock).getAuthorById(anyString());
    }

    <T> T readJson(Class<T> type) throws IOException {
        var name = "/mockdata/openlibrary/%s.json".formatted(type.getSimpleName().toLowerCase());
        var url = OpenLibraryMock.class.getResource(name);
        return objectMapper.readValue(url, type);
    }
}
