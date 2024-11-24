package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.domain.AuthorEntity;
import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.ISBN;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Set<String> LANGUAGE_TAGS = Arrays.stream(Locale.getAvailableLocales())
            .map(Locale::toLanguageTag)
            .collect(Collectors.toSet());

    @Mapping(target = "languages", source = "languages", qualifiedByName = "languages")
    BookEntity map(Book book);

    @Mapping(target = "languages", source = "languages", qualifiedByName = "languages")
    Book map(BookEntity bookEntity);

    default ISBN mapIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return null;
        }
        return ISBN.fromString(isbn);
    }

    default String mapIsbn(ISBN src) {
        return src != null ? src.toString() : null;
    }

    default AuthorEntity mapAuthor(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        AuthorEntity author = new AuthorEntity();
        author.name = name;
        return author;
    }

    default String mapAuthorName(AuthorEntity src) {
        return src != null ? src.name : null;
    }

    @Named("languages")
    default String mapLanguages(List<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return null;
        }
        return String.join(",", languages);
    }

    @Named("languages")
    default List<String> mapLanguages(String languages) {
        if (languages == null || languages.isBlank()) {
            return null;
        }

        return Arrays.stream(languages.split(","))
                .map(String::trim)
                .filter(LANGUAGE_TAGS::contains)
                .distinct()
                .sorted()
                .toList();
    }

}
