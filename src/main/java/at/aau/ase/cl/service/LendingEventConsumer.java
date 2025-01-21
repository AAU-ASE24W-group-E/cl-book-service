package at.aau.ase.cl.service;

import at.aau.ase.cl.api.model.BookStatus;
import at.aau.ase.cl.event.LendingEvent;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class LendingEventConsumer {
    public static final String LENDING_EVENT_CHANNEL = "lending";

    @Inject
    BookOwnerService bookOwnerService;

    @Incoming(LENDING_EVENT_CHANNEL)
    public Uni<LendingEvent> consumeLendingEvent(LendingEvent event) {
        Log.debugf("Received event: %s", event);
        return Uni.createFrom().item(event)
                .emitOn(Infrastructure.getDefaultExecutor())
                .invoke(this::processLendingEvent)
                .onFailure().invoke(err -> Log.error("Failed to process event " + event, err));
    }

    void processLendingEvent(LendingEvent event) {
        BookStatus status = switch (event.status()) {
            case BORROWED -> BookStatus.LENT;
            case OWNER_CONFIRMED_RETURNAL, OWNER_DENIED, READER_WITHDREW -> BookStatus.AVAILABLE;
            default -> null;
        };
        if (status != null) {
            bookOwnerService.updateBookStatus(event.bookId(), event.ownerId(), status);
        }
    }
}
