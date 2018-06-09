package com.ahajizadeh.rts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.ahajizadeh.rts.Consts.MAX_TIME_SPAN_SECONDS;

/**
 * @author amir
 */
@Service
public class StatsCalculator {
    private  static final Logger LOGGER = LoggerFactory.getLogger(StatsCalculator.class);

    private long lastRemovingOutdatedTimestamp;
    private StatsRepo statsRepo;
    private final Object newStatsLock = new Object();
    private final Object removingOutdatedLock = new Object();

    @Autowired
    public StatsCalculator(StatsRepo statsRepo) {
        this.statsRepo = statsRepo;
    }

    public StatsEntity getStats(long currentTimestampMillis) {

        statsRepo.deleteEntitiesWithTimestampLessThan(getMinAcceptableTimeSecs(currentTimestampMillis / 1000));

        int size = statsRepo.countAll();
        LOGGER.info("Getting stats...");
        LOGGER.info("total size: {}", size);

        List<StatsEntity> statsEntities = statsRepo.findAll();
        if (size > 0) {
            StatsEntity firstEntity = statsEntities.get(0);
            StatsEntity result = new StatsEntity(firstEntity.getSum(), firstEntity.getMax(), firstEntity.getMin(), firstEntity.getCount(), firstEntity.getAvg());
            for (int i = 1; i < size; i++) {
                result.setSum(result.getSum() + statsEntities.get(i).getSum());
                result.setMax(result.getMax() < statsEntities.get(i).getMax() ? statsEntities.get(i).getMax() : result.getMax());
                result.setMin(result.getMin() > statsEntities.get(i).getMin() ? statsEntities.get(i).getMin() : result.getMin());
                result.setCount(result.getCount() + statsEntities.get(i).getCount());
            }
            result.setAvg(result.getCount() > 0 ? result.getSum() / result.getCount() : 0);
            return result;
        } else {
            return new StatsEntity();
        }
    }

    public void updateStats(Transaction transaction, long currentTimestampMillis) {
        saveTransaction(transaction,currentTimestampMillis);
        deleteOutdatedEntitiesIfNeeded(currentTimestampMillis / 1000);
    }

    public long getMinAcceptableTimeSecs(long currentTimeSecs) {
        return currentTimeSecs - MAX_TIME_SPAN_SECONDS.getValue() + 1;
    }

    private void saveTransaction(Transaction transaction,long currentTimestampMillis) {
        if (transaction.getTimestamp() < currentTimestampMillis
                && transaction.getTimestamp() / 1000 >= getMinAcceptableTimeSecs(currentTimestampMillis / 1000)) {
            Long transactionTimeSecs = transaction.getTimestamp() / 1000;
            StatsEntity statsEntity;
            statsEntity = statsRepo.findByTimestamp(transactionTimeSecs);
            if (statsEntity == null) {
                synchronized (newStatsLock) {
                    statsEntity = statsRepo.findByTimestamp(transactionTimeSecs);
                    if (statsEntity == null) {
                        statsEntity = new StatsEntity();
                        statsEntity.setSum(transaction.getAmount());
                        statsEntity.setMax(transaction.getAmount());
                        statsEntity.setMin(transaction.getAmount());
                        statsEntity.setCount(1);
                        statsEntity.setAvg(transaction.getAmount());
                        statsRepo.save(transactionTimeSecs, statsEntity);
                    } else {
                        updateStatsFromTransaction(statsEntity,transaction);
                    }
                }
            } else {
                synchronized (statsEntity) {
                    updateStatsFromTransaction(statsEntity,transaction);
                }
            }
        } else {
            throw new InvalidTransactionException("Not validated!");
        }
    }

    private void updateStatsFromTransaction(StatsEntity statsEntity, Transaction transaction) {
        final double newSum = statsEntity.getSum() + transaction.getAmount();
        final long newCount = statsEntity.getCount() + 1;
        statsEntity.setMax(statsEntity.getMax() < transaction.getAmount() ? transaction.getAmount() : statsEntity.getMax());
        statsEntity.setMin(statsEntity.getMin() > transaction.getAmount() ? transaction.getAmount() : statsEntity.getMin());
        statsEntity.setSum(newSum);
        statsEntity.setAvg(newSum / newCount);
        statsEntity.setCount(newCount);
    }

    private void deleteOutdatedEntitiesIfNeeded(long currentTimeSecs) {
        if (lastRemovingOutdatedTimestamp == 0) {
            lastRemovingOutdatedTimestamp = currentTimeSecs;
        }
        if (lastRemovingOutdatedTimestamp < getMinAcceptableTimeSecs(currentTimeSecs)) {
            synchronized (removingOutdatedLock) {
                if (lastRemovingOutdatedTimestamp < getMinAcceptableTimeSecs(currentTimeSecs)) {
                    LOGGER.info("Deleting outdated entities... current repo size: {}", statsRepo.countAll());
                    statsRepo.deleteEntitiesWithTimestampLessThan(getMinAcceptableTimeSecs(currentTimeSecs));
                    lastRemovingOutdatedTimestamp = currentTimeSecs;
                    LOGGER.info("Deleting outdated entities is done on timestamps less than {} seconds", getMinAcceptableTimeSecs(currentTimeSecs));
                    LOGGER.info("Current time in seconds: {}", currentTimeSecs);
                }
            }
        }
    }
}
