package at.aau.ase.cl.client.openlibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Document(
    String title,
    @JsonProperty("author_name")
    String[] authorName,
    @JsonProperty("author_key")
    String authorKey,
    String key,
    String[] isbn,
    String[] language,
    String[] format
) {
}
