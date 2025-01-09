package at.aau.ase.cl.mapper;

import at.aau.ase.cl.api.model.BookOwner;
import at.aau.ase.cl.domain.BookOwnerEntity;
import at.aau.ase.cl.domain.GeoLocation;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BookOwnerMapperTest {

    @Test
    void mapFromModelToEntity() {
        UUID id = UUID.randomUUID();
        BookOwner model = new BookOwner(UUID.randomUUID(), "Test", 46.615691, 14.265178);
        var mapped = BookOwnerMapper.INSTANCE.map(id, model);
        assertEquals(id, mapped.id);
        assertEquals(model.name(), mapped.name);
        assertEquals(model.latitude(), mapped.location.getLatitude());
        assertEquals(model.longitude(), mapped.location.getLongitude());
    }

    @Test
    void mapFromEntityToModel() {
        BookOwnerEntity entity = new BookOwnerEntity();
        entity.id = UUID.randomUUID();
        entity.name = "Test";
        entity.location = new GeoLocation(46.615691, 14.265178);
        var mapped = BookOwnerMapper.INSTANCE.map(entity);
        assertEquals(entity.id, mapped.id());
        assertEquals(entity.name, mapped.name());
        assertEquals(entity.location.getLatitude(), mapped.latitude());
        assertEquals(entity.location.getLongitude(), mapped.longitude());
    }

    @Test
    void mapLocation() {
        BookOwner src = new BookOwner(null, null, 46.615691, 14.265178);
        var mapped = BookOwnerMapper.INSTANCE.mapLocation(src);
        assertEquals(src.latitude(), mapped.getLatitude());
        assertEquals(src.longitude(), mapped.getLongitude());
    }
}