package at.aau.ase.cl.client.openlibrary.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// see https://openlibrary.org/dev/docs/api/authors
public record Author(
        KeyValue type,
        @JsonProperty("personal_name")
        String personalName,
        @JsonProperty("alternate_names")
        List<String> alternateNames,
        String name,
        String key
) {
}
