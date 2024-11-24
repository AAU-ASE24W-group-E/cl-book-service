package at.aau.ase.cl.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ISBNTest {

    @Test
    void fromStringShouldParseValidISBN13() {
        var isbn = ISBN.fromString("978-0-618-34399-7");
        assertNotNull(isbn);
        assertEquals(9780618343997L, isbn.toLong());
    }

    @Test
    void fromStringShouldParseValidISBN10andConvertToISBN13() {
        var isbn = ISBN.fromString("0-19-852663-6");
        assertNotNull(isbn);
        assertEquals(9780198526636L, isbn.toLong());
    }

    @Test
    void fromStringShouldFailForInvalidISBN() {
        assertThrows(IllegalArgumentException.class,
                () -> ISBN.fromString("978-0-618-34399-8"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"978-0-618-34399-7", "0-19-852663-6"})
    void toStringShouldReturnOriginalFormattedISBN(String input) {
        var isbn = ISBN.fromString(input);
        assertEquals(input, isbn.toString());
    }

    @Test
    void toPlainStringShouldReturnValueWithoutSeparators() {
        var isbn = ISBN.fromString("978-0-618-34399-7");
        assertEquals("9780618343997", isbn.toPlainString());
    }

    @Test
    void toPlainStringShouldReturnStringRepresentationOfLongValue() {
        var isbn = ISBN.fromString("978-0-618-34399-7");
        var asPlainString = isbn.toPlainString();
        var asLong = isbn.toLong();
        assertEquals(Long.toString(asLong), asPlainString);
    }

    @Test
    void equalsShouldIgnoreFormatting() {
        var isbn1 = ISBN.fromString("978-0-618-34399-7");
        var isbn2 = ISBN.fromString("9780618343997");
        assertEquals(isbn1, isbn2);
    }

    @Test
    void hasCodeShouldConsiderOnlyNumber() {
        var isbn1 = ISBN.fromString("978-0-618-34399-7");
        var isbn2 = ISBN.fromString("9780618343997");
        assertEquals(isbn1.hashCode(), isbn2.hashCode());
    }
}