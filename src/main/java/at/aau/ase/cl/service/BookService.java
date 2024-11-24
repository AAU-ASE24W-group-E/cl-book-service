package at.aau.ase.cl.service;

import at.aau.ase.cl.client.openlibrary.OpenLibraryClient;
import at.aau.ase.cl.client.openlibrary.model.AuthorKey;
import at.aau.ase.cl.domain.AuthorEntity;
import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.ISBN;
import at.aau.ase.cl.mapper.OpenLibraryMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class BookService {

    @Inject
    @RestClient
    OpenLibraryClient openLibraryClient;

    @Transactional
    public BookEntity createBook(BookEntity book) {
        // create book
        book.id = UUID.randomUUID();
        for (int i = 0; i < book.authors.size(); i++) {
            var author = book.authors.get(i);
            assert author.isNew();
            var key = author.computeKey();
            AuthorEntity existing = AuthorEntity.findByKey(key);
            if (existing != null) {
                book.authors.set(i, existing);
            } else {
                author.id = UUID.randomUUID();
                author.persist();
            }
        }
        book.persist();
        return book;
    }

    public BookEntity getBookById(UUID id) {
        BookEntity book = BookEntity.findById(id);
        if (book == null) {
            Log.debugf("Book with id %s not found", id);
            throw new NotFoundException("Book with id " + id + " not found");
        }
        Log.debugf("Book with id %s found: %s", id, book);
        return book;
    }

    public BookEntity importBookByIsbn(String isbnString) {
        ISBN isbn = ISBN.fromString(isbnString);
        BookEntity book = BookEntity.findByIsbn(isbn);
        if (book != null) {
            Log.infof("Book with isbn %s already exists: %s", isbnString, book);
            return book;
        }
        // fetch book
        var olBook = openLibraryClient.getBookByIsbn(isbn.toPlainString());
        book = OpenLibraryMapper.INSTANCE.mapBook(olBook);
        book.isbn = isbn;
        // fetch authors vis works
        book.authors = olBook.works().stream()
                .map(k -> OpenLibraryMapper.INSTANCE.keyToId("works", k))
                .filter(Objects::nonNull)
                .map(openLibraryClient::getWorkById)
                .flatMap(w -> w.authors().stream())
                .map(this::importAuthor)
                .filter(Objects::nonNull)
                .toList();
        // create book
        book = createBook(book);
        Log.infof("Imported book with isbn %s: %s", isbnString, book);
        return book;
    }

    AuthorEntity importAuthor(AuthorKey authorKey) {
        var authorId = OpenLibraryMapper.INSTANCE.keyToId("authors", authorKey.author());
        if (authorId == null) {
            return null;
        }
        try {
            var olAuthor = openLibraryClient.getAuthorById(authorId);
            return OpenLibraryMapper.INSTANCE.mapAuthor(olAuthor);
        } catch (RuntimeException e) {
            Log.warn("Failed to fetch author: " + authorKey, e);
            return null;
        }
    }

    @Transactional
    public BookEntity getBookByIsbn(String isbnString) {
        ISBN isbn = ISBN.fromString(isbnString);
        var book = BookEntity.findByIsbn(isbn);
        if (book == null) {
            throw new NotFoundException("Book with isbn " + isbn + " not found");
        }
        return book;
    }
}
