package com.ahajizadeh.rts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author amir
 */
@Component
public class InMemoryStatsRepo implements StatsRepo {
    private  static final Logger LOGGER = LoggerFactory.getLogger(InMemoryStatsRepo.class);

    private SortedMap<Long,StatsEntity> transactions = new TreeMap<>();

    @Override
    public StatsEntity save(Long timestampSecs, StatsEntity statsEntity) {
        try {
            transactions.put(timestampSecs, statsEntity);
        } catch (Exception e) {
            LOGGER.info("failed: {}", timestampSecs);
        }
        return statsEntity;
    }

    @Override
    public StatsEntity findByTimestamp(Long timestampSecs) {
        return transactions.get(timestampSecs);
    }

    @Override
    public List<StatsEntity> findAll() {
        return new ArrayList<>(transactions.values());
    }

    @Override
    public void deleteEntitiesWithTimestampLessThan(Long timestampSecs) {
        ArrayList<Long> mustDeletedKeys = new ArrayList<>(transactions.headMap(timestampSecs).keySet());
        mustDeletedKeys.forEach(key -> transactions.remove(key));
    }

    @Override
    public int countAll() {
        return transactions.size();
    }

}
