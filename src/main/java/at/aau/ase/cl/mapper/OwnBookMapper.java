package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.OwnBook;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper
public interface OwnBookMapper {
    OwnBookMapper INSTANCE = Mappers.getMapper(OwnBookMapper.class);

    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "bookId", source = "bookId")
    BookOwnershipEntity map(UUID ownerId, UUID bookId, OwnBook src);

    OwnBook map(BookOwnershipEntity src);

    List<OwnBook> map(List<BookOwnershipEntity> src);
}
