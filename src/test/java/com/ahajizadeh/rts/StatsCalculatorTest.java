package com.ahajizadeh.rts;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static com.ahajizadeh.rts.Consts.MAX_TIME_SPAN_SECONDS;
import static org.junit.Assert.assertEquals;

public class StatsCalculatorTest {
    private StatsCalculator statsCalculator;
    private long[] timestamps;

    @Test
    public void save300000Transactions_TransactionsSpanMoreThan1Minute_getStatsReturnsPrecisely() {
        List<Transaction> all = new ArrayList<>();
        List<Transaction> fakeTransactions = new ArrayList<>();
        for (int i=0; i<3; i++) {
            long currentTimestamp = timestamps[i];
            LongStream.generate(() -> ThreadLocalRandom.current().nextLong(getTimeLowerBandMillis(currentTimestamp),currentTimestamp)).limit(100000)
                    .forEach(t ->
                            fakeTransactions.add(new Transaction(ThreadLocalRandom.current().nextDouble(100.0, 500.0),t))
                    );
            fakeTransactions.parallelStream().forEach(txn -> statsCalculator.updateStats(txn,currentTimestamp));
            all.addAll(fakeTransactions);
            fakeTransactions.clear();
        }

        long startTime = System.currentTimeMillis();
        StatsEntity resultStats = statsCalculator.getStats(timestamps[2]);
        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("getStats elapsed time: " + elapsedTime);

        DoubleSummaryStatistics expectedStats = all.stream().filter(txn -> txn.getTimestamp() / 1000 >= statsCalculator.getMinAcceptableTimeSecs(timestamps[2]/1000))
                .collect(Collectors.summarizingDouble(Transaction::getAmount));
        assertEquals(expectedStats.getSum(),resultStats.getSum(),0.001);
        assertEquals(expectedStats.getMax(),resultStats.getMax(),0.001);
        assertEquals(expectedStats.getMin(),resultStats.getMin(),0.001);
        assertEquals(expectedStats.getAverage(),resultStats.getAvg(),0.001);
        assertEquals(expectedStats.getCount(),resultStats.getCount());
    }

    @Before
    public void init() {
        long currentTimestamp = System.currentTimeMillis();
        timestamps = new long[]{currentTimestamp, currentTimestamp + 30000, currentTimestamp + 65000};
        statsCalculator = new StatsCalculator(new InMemoryStatsRepo());
    }

    private long getTimeLowerBandMillis(long currentTimeMillis) {
        return currentTimeMillis - (MAX_TIME_SPAN_SECONDS.getValue() - 1) * 1000;
    }
}
