package at.aau.ase.cl.api.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

public record AvailableBook(
        Book book,
        BookOwner owner,
        boolean lendable,
        boolean giftable,
        boolean exchangeable,
        BookStatus status,

        @Schema(description = "Distance in kilometers")
        double distance
) {
}
