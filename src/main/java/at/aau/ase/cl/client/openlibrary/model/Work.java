package at.aau.ase.cl.client.openlibrary.model;


import java.util.List;

public record Work(
        String title,
        String key,
        List<AuthorKey> authors
) {
}
