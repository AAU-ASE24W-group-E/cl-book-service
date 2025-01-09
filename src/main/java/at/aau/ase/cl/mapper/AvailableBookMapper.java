package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.FindAvailableBooksParams;
import at.aau.ase.cl.domain.AvailableBooksSearchCriteria;
import at.aau.ase.cl.domain.GeoLocation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AvailableBookMapper {
    AvailableBookMapper INSTANCE = Mappers.getMapper(AvailableBookMapper.class);

    AvailableBooksSearchCriteria map(FindAvailableBooksParams params);

    default GeoLocation mapLocation(String src) {
        return GeoLocation.fromString(src);
    }
}
