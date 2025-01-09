package at.aau.ase.cl.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvailableBooksSearchCriteriaTest {

    AvailableBooksSearchCriteria model;

    @BeforeEach
    void setUp() {
        model = new AvailableBooksSearchCriteria();
    }

    @Test
    void parseQuickSearchShouldIgnoreNull() {
        model.parseQuickSearch(null);
        assertEquals(List.of(), model.quickSearchTerms);
    }

    @Test
    void parseQuickSearchShouldIgnoreWhitespace() {
        model.parseQuickSearch(" \t\n");
        assertEquals(List.of(), model.quickSearchTerms);
    }

    @Test
    void parseQuickSearchShouldIgnoreShortTerms() {
        model.parseQuickSearch("aaaa bbb cc d\n\te");
        assertEquals(List.of("aaaa"), model.quickSearchTerms);
    }

    @Test
    void parseQuickSearchShouldIgnoreDuplicates() {
        model.parseQuickSearch("aaaa  aaaa  bbbb   BBBB  cccc Cccc");
        assertEquals(List.of("aaaa", "bbbb", "cccc"), model.quickSearchTerms);
    }
}