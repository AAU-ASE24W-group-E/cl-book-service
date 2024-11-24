package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.BookFormat;
import at.aau.ase.cl.client.openlibrary.model.Author;
import at.aau.ase.cl.client.openlibrary.model.Book;
import at.aau.ase.cl.client.openlibrary.model.KeyValue;
import at.aau.ase.cl.domain.AuthorEntity;
import at.aau.ase.cl.domain.BookEntity;
import io.quarkus.logging.Log;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface OpenLibraryMapper {
    OpenLibraryMapper INSTANCE = Mappers.getMapper(OpenLibraryMapper.class);

    Map<BookFormat, Set<String>> ALTERNATIVE_FORMATS = Map.of(
            BookFormat.AUDIOBOOK, Set.of("AUDIO", "CD", "MP3"),
            BookFormat.EBOOK, Set.of("ELECTRONIC", "EPUB", "PDF", "MOBI"));

    @Mapping(target = "publishYear", source = "publishDate")
    @Mapping(target = "publisher", source = "publishers", qualifiedByName = "publisher")
    @Mapping(target = "languages", source = "languages", qualifiedByName = "languages")
    @Mapping(target = "coverId", source = "covers", qualifiedByName = "cover")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authors", ignore = true)
    BookEntity mapBook(Book src);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "key", ignore = true)
    AuthorEntity mapAuthor(Author src);

    default Integer mapYearFromDate(String src) {
        if (src == null || src.isBlank()) {
            return null;
        }
        if (src.matches("\\d{4}-\\d{2}-\\d{2}|\\d{4}")) {
            return Integer.parseInt(src.substring(0, 4));
        }
        Log.warn("Invalid date format: " + src);
        return null;
    }

    @Named("publisher")
    default String mapPublisher(List<String> publishers) {
        if (publishers == null || publishers.isEmpty()) {
            return null;
        }
        return publishers.getFirst();
    }

    @Named("cover")
    default String mapCoverId(int[] covers) {
        if (covers == null || covers.length == 0) {
            return null;
        }
        return Integer.toString(covers[0]);
    }

    default BookFormat mapFormat(String src) {
        if (src == null || src.isBlank()) {
            return null;
        }
        String ciFormat = src.replaceAll("\\W+", "").toUpperCase();
        for (var format : BookFormat.values()) {
            if (ciFormat.contains(format.name())) {
                return format;
            }
        }
        // try alternative formats
        for (var alternative : ALTERNATIVE_FORMATS.entrySet()) {
            if (alternative.getValue().stream().anyMatch(ciFormat::contains)) {
                return alternative.getKey();
            }
        }
        Log.warnf("Ignoring unknown format: %s", src);
        return null;
    }

    @Named("languages")
    default String mapLanguages(List<KeyValue> languages) {
        if (languages == null || languages.isEmpty()) {
            return null;
        }
        return languages.stream()
                .map(this::mapLanguage)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.joining(","));
    }

    /**
     * convert ISO 639 alpha-3 to alpha-2
     * @param src language key, e.g. "/languages/eng"
     * @return language code, e.g. "en"
     */
    default String mapLanguage(KeyValue src) {
        // map only top 10 most popular languages, see https://openlibrary.org/languages
        return switch(keyToId("languages", src)) {
            case "eng" -> "en";
            case "deu", "ger" -> "de";
            case "fra", "fre" -> "fr";
            case "spa" -> "es";
            case "rus" -> "ru";
            case "ita" -> "it";
            case "chi", "zho" -> "zh";
            case "jpn" -> "ja";
            case "ara", "arb" -> "ar";
            case "por" -> "pt";
            case null, default -> null;
        };
    }

    default String keyToId(String prefix, KeyValue src) {
        if (src == null || src.key() == null) {
            return null;
        }
        String value = src.key();
        String normalizedPrefix = prefix;
        if (!normalizedPrefix.startsWith("/")) {
            normalizedPrefix = "/" + normalizedPrefix;
        }
        if (!normalizedPrefix.endsWith("/")) {
            normalizedPrefix += "/";
        }
        if (value.startsWith(normalizedPrefix)) {
            return value.substring(normalizedPrefix.length());
        }
        Log.warnf("Ignoring invalid key '%s' because of one '%s' expected!", value, prefix);
        return null;
    }

}
