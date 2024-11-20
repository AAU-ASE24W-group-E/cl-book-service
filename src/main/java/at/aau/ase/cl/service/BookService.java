package at.aau.ase.cl.service;

import at.aau.ase.cl.model.AuthorEntity;
import at.aau.ase.cl.model.BookEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class BookService {

    @Transactional
    public BookEntity createBook(BookEntity book) {
        // create book
        book.id = UUID.randomUUID();
        for(int i = 0; i < book.authors.size(); i++) {
            var author = book.authors.get(i);
            if (author.isNew()) {
                var key = author.computeKey();
                AuthorEntity existing = AuthorEntity.find("key", key).firstResult();
                if (existing != null) {
                    book.authors.set(i, existing);
                } else {
                    author.id = UUID.randomUUID();
                    author.persist();
                }
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
}
