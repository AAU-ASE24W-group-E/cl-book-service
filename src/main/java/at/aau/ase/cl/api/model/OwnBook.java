package at.aau.ase.cl.api.model;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

public record OwnBook(
        @Schema(readOnly = true, description = "The user ID of the owner of the book")
        UUID ownerId,

        @Schema(readOnly = true, description = "The book")
        Book book,

        @Schema(description = "Whether the book can be lent to other users", defaultValue = "false")
        boolean lendable,

        @Schema(description = "Whether the book can be gifted to other users", defaultValue = "false")
        boolean giftable,

        @Schema(description = "Whether the book can be exchanged with other users", defaultValue = "false")
        boolean exchangable,

        @Schema(description = "Current status of the book")
        BookStatus status
) {
    public OwnBook(boolean lendable, boolean giftable, boolean exchangable, BookStatus status) {
        this(null, null, lendable, giftable, exchangable, status);
    }
}
