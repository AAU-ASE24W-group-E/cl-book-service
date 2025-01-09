package at.aau.ase.cl.api.model;

import java.util.List;

public record FindAvailableBooksResponse(
        List<AvailableBook> results,
        boolean hasMore
) {
}
