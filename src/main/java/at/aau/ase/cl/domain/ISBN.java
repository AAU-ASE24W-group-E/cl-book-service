package at.aau.ase.cl.domain;

import io.quarkus.logging.Log;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * ISBN
 * see <a href="https://isbn-information.com/convert-isbn-10-to-isbn-13.html">ISBN 10 to ISBN 13</a>
 */
@Embeddable
public class ISBN {
    @Column(name = "isbn13")
    long number;

    @Column(name = "isbn_original")
    String isbn;

    public ISBN() {
    }

    ISBN(long number, String isbn) {
        this.number = number;
        this.isbn = isbn;
    }

    /**
     * Create ISBN from string representation.
     * The ISBN-10 input is supported, but always converted to ISBN-13.
     * @param isbn ISBN-10 or ISBN-13 string representation
     * @return ISBN instance
     */
    public static ISBN fromString(String isbn) {
        var num = stripSeparators(isbn);
        return switch (num.length()) {
            case 10 -> new ISBN(convert10to13(num), isbn);
            case 13 -> {
                validateIsbn13(num);
                yield new ISBN(Long.parseLong(num), isbn);
            }
            default -> throw new IllegalArgumentException("Invalid ISBN length: " + num.length());
        };
    }

    static String stripSeparators(String isbn) {
        return isbn.replaceAll("[^0-9X]+", "");
    }

    static long convert10to13(String isbn10) {
        // see https://isbn-information.com/convert-isbn-10-to-isbn-13.html
        if (isbn10.length() != 10) {
            throw new IllegalArgumentException("Invalid ISBN-10 length: " + isbn10.length());
        }
        validateIsbn10(isbn10);
        var num = "978" + isbn10.substring(0, 9);
        int rem = calculateIsbn13ChecksumRemainder(num);
        int checksum = rem == 0 ? 0 : 10 - rem;
        num += checksum;
        return Long.parseLong(num);
    }

    static void validateIsbn10(String isbn10) {
        if (isbn10.length() != 10) {
            throw new IllegalArgumentException("Invalid ISBN-10 length: " + isbn10.length());
        }
        char[] digits = isbn10.toCharArray();
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            int w = 10 - i;
            int d = digits[i] == 'X' ? 10 : digits[i] - '0';
            sum += w * d;
        }
        int remainder = sum % 11;
        if (remainder != 0) {
            Log.debugf("Invalid ISBN-10 checksum: %s (remainder: %d)", isbn10, remainder);
            throw new IllegalArgumentException("Invalid ISBN-10 checksum: " + isbn10);
        }
    }

    static void validateIsbn13(String isbn13) {
        if (isbn13.length() != 13) {
            throw new IllegalArgumentException("Invalid ISBN-13 length: " + isbn13.length());
        }
        int rem = calculateIsbn13ChecksumRemainder(isbn13);
        if (rem != 0) {
            Log.debugf("Invalid ISBN-13 checksum: %s (remainder: %d)", isbn13, rem);
            throw new IllegalArgumentException("Invalid ISBN-13 checksum: " + isbn13);
        }
    }

    static int calculateIsbn13ChecksumRemainder(String isbn) {
        // the method is used as to calculate as to validate checksum with 12 or 13 digits
        char[] digits = isbn.toCharArray();
        int sum = 0;
        for (int i = 0; i < digits.length; i++) {
            int w = i % 2 == 0 ? 1 : 3;
            int d = digits[i] - '0';
            sum += w * d;
        }
        return sum % 10;
    }

    public String toString() {
        return isbn;
    }

    public String toPlainString() {
        return Long.toString(number);
    }

    public long toLong() {
        return number;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ISBN && ((ISBN) obj).number == number;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(number);
    }
}
