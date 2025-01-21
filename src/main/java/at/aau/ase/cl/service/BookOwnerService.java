package at.aau.ase.cl.service;

import at.aau.ase.cl.api.model.BookSortingProperty;
import at.aau.ase.cl.api.model.BookStatus;
import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import at.aau.ase.cl.mapper.OwnBookMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class BookOwnerService {
    @Transactional
    public void updateBookOwner(BookOwnerEntity owner) {
        BookOwnerEntity entity = BookOwnerEntity.findById(owner.id);
        if (entity == null) {
            owner.persistAndFlush();
            Log.debugf("Creating new owner %s", owner.id);
        } else {
            // update writable fields
            entity.name = owner.name;
            entity.location = owner.location;
            Log.debugf("Updating owner %s", owner.id);
        }
    }

    @Transactional
    public void updateBookOwnership(BookOwnershipEntity ownBook) {
        var entity = BookOwnershipEntity.findByOwnerAndBook(ownBook.ownerId, ownBook.bookId);
        if (entity == null) {
            throw new NotFoundException("Ownership not found");
        }
        // update writable fields
        OwnBookMapper.INSTANCE.update(ownBook, entity);
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

    @Transactional
    public BookOwnerEntity getBookOwner(UUID id) {
        return BookOwnerEntity.findById(id);
    }

    @Transactional
    public List<BookOwnershipEntity> getOwnBooks(UUID ownerId, BookSortingProperty sort, Boolean descending) {
        return BookOwnershipEntity.findByOwner(ownerId, sort, descending);
    }

    @Transactional
    public void updateBookStatus(UUID bookId, UUID ownerId, BookStatus status) {
        var entity = BookOwnershipEntity.findByOwnerAndBook(ownerId, bookId);
        if (entity == null) {
            Log.warnf("Book %s not found for owner %s", bookId, ownerId);
            return;
        }
        Log.infof("Updating status of book %s of owner %s from %s to %s", bookId, ownerId, entity.status, status);
        entity.status = status;
    }
}
