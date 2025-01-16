package at.aau.ase.cl.event;

import java.util.UUID;

public record UserEvent(
        UUID id,
        String username,
        Double latitude,
        Double longitude
) {
}
