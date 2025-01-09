package at.aau.ase.cl.service;

import at.aau.ase.cl.api.model.AvailableBook;
import at.aau.ase.cl.api.model.FindAvailableBooksParams;
import at.aau.ase.cl.api.model.FindAvailableBooksResponse;
import at.aau.ase.cl.domain.AvailableBooksSearchCriteria;
import at.aau.ase.cl.domain.BookOwnershipEntity;
import at.aau.ase.cl.mapper.OwnBookMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class AvailableBookService {
    @Transactional
    public FindAvailableBooksResponse findAvailableBooks(FindAvailableBooksParams params) {
        AvailableBooksSearchCriteria criteria = OwnBookMapper.INSTANCE.map(params);
        criteria.parseQuickSearch(params.quickSearch());

        var entities = BookOwnershipEntity.findAvailableBooks(criteria);

        Log.debugf("Found %d available books by criteria %s", entities.size(), params);
        List<AvailableBook> results = entities.stream()
                .limit(criteria.limit)
                .map(OwnBookMapper.INSTANCE::mapAvailableBook)
                .toList();
        boolean hasMore = criteria.limit < entities.size();
        return new FindAvailableBooksResponse(results, hasMore);
    }
}
