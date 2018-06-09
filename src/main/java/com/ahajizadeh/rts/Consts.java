package com.ahajizadeh.rts;

/**
 * @author amir
 */
public enum Consts {
    MAX_TIME_SPAN_SECONDS(60);

    private final int value;

    Consts(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
