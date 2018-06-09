package com.ahajizadeh.rts;

import java.util.List;

/**
 * @author amir
 */
public interface StatsRepo {
    StatsEntity save(Long timestampSecs, StatsEntity statsEntity);
    StatsEntity findByTimestamp(Long timestampSecs);
    List<StatsEntity> findAll();
    void deleteEntitiesWithTimestampLessThan(Long timestampSecs);
    int countAll();
}
