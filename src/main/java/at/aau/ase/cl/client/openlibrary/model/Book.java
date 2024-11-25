package at.aau.ase.cl.client.openlibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Book(
        String title,
        List<String> publishers,
        @JsonProperty("publish_date")
        String publishDate,
        String key,
        List<KeyValue> languages,
        @JsonProperty("edition_name")
        String edition,
        @JsonProperty("physical_format")
        String format,
        List<Integer> covers,
        List<KeyValue> works
) {
}
