package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.AvailableBook;
import at.aau.ase.cl.api.model.OwnBook;
import at.aau.ase.cl.domain.AvailableBookProjection;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;

@Mapper(uses = {BookMapper.class, BookOwnerMapper.class})
public interface OwnBookMapper {
    OwnBookMapper INSTANCE = Mappers.getMapper(OwnBookMapper.class);

    @Mapping(target = "ownerId", source = "ownerId")
    @Mapping(target = "bookId", source = "bookId")
    @Mapping(target = "book", ignore = true)
    BookOwnershipEntity map(UUID ownerId, UUID bookId, OwnBook src);

    @Mapping(target = "ownerId", source = "owner.id")
    OwnBook map(BookOwnershipEntity src);

    List<OwnBook> map(List<BookOwnershipEntity> src);

    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "book", ignore = true)
    void update(BookOwnershipEntity src, @MappingTarget BookOwnershipEntity target);

    @Mapping(target = "lendable", source = "bookOwnership.lendable")
    @Mapping(target = "exchangeable", source = "bookOwnership.exchangeable")
    @Mapping(target = "giftable", source = "bookOwnership.giftable")
    @Mapping(target = "status", source = "bookOwnership.status")
    @Mapping(target = "distance", source = "roundedDistanceKm")
    AvailableBook mapAvailableBook(AvailableBookProjection src);
}
