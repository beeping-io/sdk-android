package com.beeping.AndroidBeepingCore;

public enum EnumBeepingMode {

    MODE_AUDIBLE(0), MODE_NONAUDIBLE(1), MODE_HIDDEN(2), MODE_ALL(3), MODE_CUSTOM(4);

    private final int value;

    EnumBeepingMode(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
