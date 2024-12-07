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
    String singleString;

    public Languages() {
    }

    Languages(String singleString) {
        this.singleString = singleString;
    }

    public static Languages of(List<String> tags) {
        return of(tags.stream());
    }

    public static Languages of(Stream<String> s) {
        var single = s.filter(LANGUAGE_TAGS::contains)
                .collect(Collectors.joining(SEPARATOR));
        return new Languages(single);
    }

    public List<String> toList() {
        return Arrays.asList(singleString.split(SEPARATOR));
    }

    @Override
    public String toString() {
        return singleString;
    }
}
