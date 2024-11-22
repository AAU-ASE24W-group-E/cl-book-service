package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.domain.AuthorEntity;
import at.aau.ase.cl.domain.BookEntity;
import at.aau.ase.cl.domain.ISBN;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

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
}
