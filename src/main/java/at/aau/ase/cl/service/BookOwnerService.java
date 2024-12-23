package at.aau.ase.cl.service;

import at.aau.ase.cl.api.model.BookStatus;
import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.UUID;

@ApplicationScoped
public class BookOwnerService {

    @Transactional
    public void updateBookOwnership(BookOwnershipEntity ownBook) {
        var entity = BookOwnershipEntity.findByOwnerAndBook(ownBook.ownerId, ownBook.bookId);
        if (entity == null) {
            throw new NotFoundException("Ownership not found");
        }
        // update writable fields
        entity.lendable = ownBook.lendable;
        entity.giftable = ownBook.giftable;
        entity.exchangable = ownBook.exchangable;
        Log.debugf("Updating ownership of book %s by user %s", ownBook.bookId, ownBook.ownerId);
    }

    @Transactional
    public BookOwnershipEntity createBookOwnership(UUID ownerId, UUID bookId) {
        var entity = BookOwnershipEntity.findByOwnerAndBook(ownerId, bookId);
        if (entity != null) {
            Log.warnf("Ownership of book %s by user %s already exists", bookId, ownerId);
            return entity;
        }

        entity = new BookOwnershipEntity();

        entity.bookId = bookId;
        entity.book = BookEntity.findById(bookId);
        if (entity.book == null) {
            throw new NotFoundException("Book not found");
        }

        entity.ownerId = ownerId;
        entity.owner = BookOwnerEntity.findById(ownerId);
        if (entity.owner == null) {
            BookOwnerEntity owner = new BookOwnerEntity();
            owner.id = ownerId;
            owner.persist();
            Log.debugf("Creating new owner %s implicitly", owner.id);
            entity.owner = owner;
        }

        entity.status = BookStatus.UNAVAILABLE;
        entity.persist();

        return entity;
    }
}
