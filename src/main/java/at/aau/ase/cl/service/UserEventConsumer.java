package at.aau.ase.cl.service;

import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.domain.GeoLocation;
import at.aau.ase.cl.event.UserEvent;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class UserEventConsumer {
    public static final String USER_EVENT_CHANNEL = "user";

    @Inject
    BookOwnerService bookOwnerService;

    @Incoming(USER_EVENT_CHANNEL)
    public Uni<UserEvent> consumeUserEvent(UserEvent event) {
        Log.debugf("Received user event: %s", event);
        return Uni.createFrom().item(event)
                .emitOn(Infrastructure.getDefaultExecutor())
                .invoke(this::processUserEvent)
                .onFailure().invoke(err -> Log.error("Failed to process user event " + event, err));
    }

    void processUserEvent(UserEvent event) {
        var owner = new BookOwnerEntity();
        owner.id = event.id();
        owner.name = event.username();
        owner.location = new GeoLocation(event.latitude(), event.longitude());
        bookOwnerService.updateBookOwner(owner);
    }
}
