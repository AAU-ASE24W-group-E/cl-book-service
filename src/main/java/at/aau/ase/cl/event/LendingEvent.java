package at.aau.ase.cl.event;

import java.util.UUID;

public record LendingEvent(
        UUID bookId,
        UUID ownerId,
        LendingStatus status
) {
}
