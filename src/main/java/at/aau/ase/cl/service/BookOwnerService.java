package at.aau.ase.cl.service;

import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

@ApplicationScoped
public class BookOwnerService {

    @Transactional
    public void createOrUpdateBookOwnership(BookOwnershipEntity ownBook) {
        var entity = BookOwnershipEntity.findByOwnerAndBook(ownBook.ownerId, ownBook.bookId);
        if (entity == null) {
            ownBook.book = BookEntity.findById(ownBook.bookId);
            if (ownBook.book == null) {
                throw new NotFoundException("Book not found");
            }
            ownBook.owner = BookOwnerEntity.findById(ownBook.ownerId);
            if (ownBook.owner == null) {
                BookOwnerEntity owner = new BookOwnerEntity();
                owner.id = ownBook.ownerId;
                owner.persist();
                Log.debugf("Creating new owner %s implicitly", owner.id);
                ownBook.owner = owner;
            }
            ownBook.persist();
            Log.debugf("Creating new ownership of book %s by user %s", ownBook.bookId, ownBook.ownerId);
        } else {
            entity.lendable = ownBook.lendable;
            entity.giftable = ownBook.giftable;
            entity.exchangable = ownBook.exchangable;
            entity.status = ownBook.status;
            Log.debugf("Updating ownership of book %s by user %s", ownBook.bookId, ownBook.ownerId);
        }
    }
}
