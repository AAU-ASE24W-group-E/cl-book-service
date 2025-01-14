package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.BookOwner;
import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.domain.GeoLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface BookOwnerMapper {
    BookOwnerMapper INSTANCE = Mappers.getMapper(BookOwnerMapper.class);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "location", source = "src", qualifiedByName = "mapLocation")
    BookOwnerEntity map(UUID id, BookOwner src);

    @Mapping(target = "latitude", source = "location.latitude")
    @Mapping(target = "longitude", source = "location.longitude")
    BookOwner map(BookOwnerEntity src);

    @Named("mapLocation")
    default GeoLocation mapLocation(BookOwner src) {
        return new GeoLocation(src.latitude(), src.longitude());
    }
}
