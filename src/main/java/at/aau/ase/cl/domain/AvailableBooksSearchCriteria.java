package at.aau.ase.cl.domain;

import java.util.Arrays;
import java.util.List;

public class AvailableBooksSearchCriteria {
    public static final int DEFAULT_DISTANCE = 10;
    public static final int DEFAULT_LIMIT = 100;

    public GeoLocation location;

    public int distance = DEFAULT_DISTANCE;

    public List<String> quickSearchTerms = List.of();

    public String title;

    public String author;

    public String isbn;

    public boolean lendable;

    public boolean exchangeable;

    public boolean giftable;

    public int offset;

    public int limit = DEFAULT_LIMIT;

    public void parseQuickSearch(String quickSearch) {
        if (quickSearch != null && !quickSearch.isBlank()) {
            var terms = quickSearch.split("\\s+");
            quickSearchTerms = Arrays.stream(terms)
                    .map(String::strip)
                    .filter(s -> s.length() >= 4)
                    .map(String::toLowerCase)
                    .distinct().toList();
        }
    }
}
