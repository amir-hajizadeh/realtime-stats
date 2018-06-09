package com.ahajizadeh.rts;

/**
 * @author amir
 */
public class StatsEntity {
    private double sum;
    private double max;
    private double min;
    private long count;
    private double avg;

    public StatsEntity() {
    }

    public StatsEntity(double sum, double max, double min, long count, double avg) {
        this.sum = sum;
        this.max = max;
        this.min = min;
        this.count = count;
        this.avg = avg;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }
}
