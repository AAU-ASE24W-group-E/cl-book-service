package at.aau.ase.cl.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Languages {
    static final Set<String> LANGUAGE_TAGS = Arrays.stream(Locale.getAvailableLocales())
            .map(Locale::toLanguageTag)
            .collect(Collectors.toSet());

    static final String SEPARATOR = ",";

    @Column(name = "languages")
    String encoded;

    public Languages() {
    }

    Languages(String encoded) {
        this.encoded = encoded;
    }

    public static Languages of(List<String> tags) {
        return of(tags.stream());
    }

    public static Languages of(Stream<String> s) {
        var enc = s.filter(LANGUAGE_TAGS::contains)
                .collect(Collectors.joining(SEPARATOR));
        return new Languages(enc);
    }

    public List<String> toList() {
        return Arrays.stream(encoded.split(SEPARATOR))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return encoded;
    }
}
