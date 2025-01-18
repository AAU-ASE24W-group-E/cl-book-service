package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.FindAvailableBooksParams;
import at.aau.ase.cl.domain.AvailableBooksSearchCriteria;
import at.aau.ase.cl.domain.GeoLocation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AvailableBookMapper {
    AvailableBookMapper INSTANCE = Mappers.getMapper(AvailableBookMapper.class);

    @Mapping(target = "location", source = "src")
    AvailableBooksSearchCriteria map(FindAvailableBooksParams src);

    default GeoLocation mapLocation(FindAvailableBooksParams src) {
        return new GeoLocation(src.latitude(), src.longitude());
    }
}
