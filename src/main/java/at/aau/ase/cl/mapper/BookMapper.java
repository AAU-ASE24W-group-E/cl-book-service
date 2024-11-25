package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.domain.AuthorEntity;
import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.ISBN;
import at.aau.ase.cl.domain.Languages;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookEntity map(Book book);

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

    default Languages mapLanguages(List<String> languages) {
        if (languages == null || languages.isEmpty()) {
            return null;
        }
        return Languages.of(languages);
    }

    default List<String> mapLanguages(Languages languages) {
        if (languages == null) {
            return List.of();
        }
        return languages.toList();
    }

}
