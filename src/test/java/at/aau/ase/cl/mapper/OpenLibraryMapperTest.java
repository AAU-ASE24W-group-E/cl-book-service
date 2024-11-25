package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.BookFormat;
import at.aau.ase.cl.client.openlibrary.model.Author;
import at.aau.ase.cl.client.openlibrary.model.Book;
import at.aau.ase.cl.client.openlibrary.model.KeyValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OpenLibraryMapperTest {
    OpenLibraryMapper mapper = OpenLibraryMapper.INSTANCE;

    @Test
    void mapBook() {
        Book book = new Book("The Title", List.of("Publisher inc"), "2024", "/books/OL123456E",
                List.of(new KeyValue("/languages/eng")), "1st edition", "paperback",
                List.of(123456), List.of(new KeyValue("/works/OL1234W")));

        var mapped = mapper.mapBook(book);

        assertEquals("The Title", mapped.title);
        assertEquals("Publisher inc", mapped.publisher);
        assertEquals(2024, mapped.publishYear);
        assertEquals("en", mapped.languages.toString());
        assertEquals("1st edition", mapped.edition);
        assertEquals(BookFormat.PAPERBACK, mapped.format);
        assertEquals("123456", mapped.coverId);
        assertNull(mapped.authors);
    }

    @Test
    void mapAuthor() {
        Author author = new Author(new KeyValue("/type/author"), "J. R. R. Tolkien",
                List.of("John Ronald Reuel Tolkien", "J. R. R. Tolkien", "John R. R. Tolkien", "Tolkien"),
                "J.R.R. Tolkien",
                "/authors/OL26320A");

        var mapped = mapper.mapAuthor(author);

        assertEquals("J.R.R. Tolkien", mapped.name);
        assertNull(mapped.id);
        assertNull(mapped.key);
    }

    @Test
    void mapYearFromDateShouldReturnYearFromYearString() {
        assertEquals(2024, mapper.mapYearFromDate("2024"));
    }

    @Test
    void mapYearFromDateShouldReturnYearFromIsoDate() {
        assertEquals(2024, mapper.mapYearFromDate("2024-12-31"));
    }

    @Test
    void mapYearFromDateShouldReturnNullForInvalidDate() {
        assertNull(mapper.mapYearFromDate("31 Dec 24"));
    }

    @Test
    void mapYearFromDateShouldReturnNullForBlankDate() {
        assertNull(mapper.mapYearFromDate(" "));
        assertNull(mapper.mapYearFromDate(""));
        assertNull(mapper.mapYearFromDate("\t"));
    }

    @Test
    void mapYearFromDateShouldReturnNullForNullDate() {
        assertNull(mapper.mapYearFromDate(null));
    }

    @Test
    void mapPublisherShouldReturnFirstEntryFromList() {
        List<String> publishers = List.of("Publisher inc", "Publisher ltd");
        var mapped = mapper.mapPublisher(publishers);
        assertEquals("Publisher inc", mapped);
    }

    @Test
    void mapPublisherShouldReturnNullForEmptyList() {
        List<String> publishers = List.of();
        var mapped = mapper.mapPublisher(publishers);
        assertNull(mapped);
    }

    @Test
    void mapPublisherShouldReturnNullForNullList() {
        List<String> publishers = null;
        var mapped = mapper.mapPublisher(publishers);
        assertNull(mapped);
    }

    @Test
    void mapCoverIdShouldReturnFirstEntryFromList() {
        var covers = List.of(123456, 789012);
        var mapped = mapper.mapCoverId(covers);
        assertEquals("123456", mapped);
    }

    @Test
    void mapCoverIdShouldReturnNullForEmptyList() {
        List<Integer> covers = List.of();
        var mapped = mapper.mapCoverId(covers);
        assertNull(mapped);
    }

    @Test
    void mapCoverIdShouldReturnNullForNullList() {
        List<Integer> covers = null;
        var mapped = mapper.mapCoverId(covers);
        assertNull(mapped);
    }

    @ParameterizedTest
    @ValueSource(strings = {"paperback", "Paperback", "PAPERBACK"})
    void mapFormatShouldMapPaperback(String format) {
        var mapped = mapper.mapFormat(format);
        assertEquals(BookFormat.PAPERBACK, mapped);
    }

    @ParameterizedTest
    @ValueSource(strings = {"hardcover", "Hardcover", "HARDCOVER"})
    void mapFormatShouldMapHardcover(String format) {
        var mapped = mapper.mapFormat(format);
        assertEquals(BookFormat.HARDCOVER, mapped);
    }

    @ParameterizedTest
    @ValueSource(strings = {"audiobook", "Audiobook", "AUDIOBOOK", "mp3 cd", "Audio CD"})
    void mapFormatShouldMapAudiobook(String format) {
        var mapped = mapper.mapFormat(format);
        assertEquals(BookFormat.AUDIOBOOK, mapped);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ebook", "Ebook", "E-Book", "EBOOK", "Electronic resource", "[electronic resource]"})
    void mapFormatShouldMapEbook(String format) {
        var mapped = mapper.mapFormat(format);
        assertEquals(BookFormat.EBOOK, mapped);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "unknown"})
    void mapFormatShouldReturnNullForNullOrBlankOrUnknownFormat(String format) {
        var mapped = mapper.mapFormat(format);
        assertNull(mapped);
    }

    @ParameterizedTest
    @CsvSource(textBlock = """
            eng,en
            deu,de
            ger,de
            fra,fr
            fre,fr
            spa,es
            rus,ru
            ita,it
            chi,zh
            zho,zh
            jpn,ja
            ara,ar
            arb,ar
            por,pt
            """)
    void mapLanguageShouldMapFromAlpha3ToAlpha2Codes(String alpha3code, String alpha2code) {
        var mapped = mapper.mapLanguage(new KeyValue("/languages/" + alpha3code));
        assertEquals(alpha2code, mapped);
    }

    @Test
    void mapLanguageShouldReturnNullForUnknownLanguages() {
        var mapped = mapper.mapLanguage(new KeyValue("/languages/unknown"));
        assertNull(mapped);
    }

    @Test
    void mapLanguageShouldReturnNullForNull() {
        KeyValue key = null;
        var mapped = mapper.mapLanguage(key);
        assertNull(mapped);
    }

    @Test
    void mapLanguageShouldReturnNullForNullLanguageKey() {
        KeyValue key = new KeyValue(null);
        var mapped = mapper.mapLanguage(key);
        assertNull(mapped);
    }
}