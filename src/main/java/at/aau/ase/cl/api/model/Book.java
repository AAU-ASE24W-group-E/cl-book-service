package at.aau.ase.cl.api.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

public record Book(
    UUID id,

    @Schema(description = "Title of the book", example = "The Lord of the Rings")
    @Size(min = 1, max = 255)
    @NotNull
    String title,

    @Schema(description = "ISBN number of the book, if available", example = "978-0-618-34399-7")
    @Pattern(regexp = "\\d+(-\\d+)+")
    @Size(min = 10, max = 17)
    String isbn,

    @Schema(description = "Languages of the book as IETF BCP 47 language tags")
    @NotNull
    @Size(min = 1)
    List<@Size(min = 2, max = 10) String> languages,

    BookFormat format,

    String publisher,

    Integer publishYear,

    String coverId,

    String edition,

    @Schema(description = "List of authors of the book")
    @NotNull
    @Size(min = 1)
    List<@Size(min = 2) String> authors
) {
}
