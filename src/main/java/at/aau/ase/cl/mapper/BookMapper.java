package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.Book;
import at.aau.ase.cl.model.BookEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BookMapper {
    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    BookEntity map(Book book);

    Book map(BookEntity bookEntity);
}
