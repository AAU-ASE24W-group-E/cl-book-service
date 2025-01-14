package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.AvailableBook;
import at.aau.ase.cl.api.model.FindAvailableBooksParams;
import at.aau.ase.cl.api.model.OwnBook;
import at.aau.ase.cl.domain.AvailableBookProjection;
import at.aau.ase.cl.domain.AvailableBooksSearchCriteria;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import at.aau.ase.cl.domain.GeoLocation;
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
    BookOwnershipEntity map(UUID ownerId, UUID bookId, OwnBook src);

    OwnBook map(BookOwnershipEntity src);

    List<OwnBook> map(List<BookOwnershipEntity> src);

    @Mapping(target = "ownerId", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "bookId", ignore = true)
    @Mapping(target = "book", ignore = true)
    void update(BookOwnershipEntity src, @MappingTarget BookOwnershipEntity target);

    AvailableBooksSearchCriteria map(FindAvailableBooksParams src);

    default GeoLocation mapLocation(String src) {
        return GeoLocation.fromString(src);
    }

    @Mapping(target = "lendable", source = "bookOwnership.lendable")
    @Mapping(target = "exchangeable", source = "bookOwnership.exchangeable")
    @Mapping(target = "giftable", source = "bookOwnership.giftable")
    @Mapping(target = "status", source = "bookOwnership.status")
    @Mapping(target = "distance", source = "roundedDistanceKm")
    AvailableBook mapAvailableBook(AvailableBookProjection src);
}
