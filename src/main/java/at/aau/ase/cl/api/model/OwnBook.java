package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.UUID;

public record OwnBook(
        @Schema(readOnly = true, description = "The user ID of the owner of the book")
        UUID ownerId,

        @Schema(readOnly = true, description = "The ID of the book")
        UUID bookId,

        @Schema(description = "Whether the book can be lent to other users", defaultValue = "false")
        boolean lendable,

        @Schema(description = "Whether the book can be gifted to other users", defaultValue = "false")
        boolean giftable,

        @Schema(description = "Whether the book can be exchanged with other users", defaultValue = "false")
        boolean exchangable,

        @NotNull
        BookStatus status
) {
}
