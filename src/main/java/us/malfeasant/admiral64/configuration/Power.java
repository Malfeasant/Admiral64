package us.malfeasant.admiral64.configuration;

import javafx.util.StringConverter;

public enum Power {
    NA(6), EU(5);

    private final int jiffiesPerTick;
    private final String hz;
    Power(int j) {
        jiffiesPerTick = j;
        hz = String.format("%dHz", jiffiesPerTick * 10);
    }
    @Override
    public String toString() {
        return hz;
    }

    // TODO find a way to generalize this for both Oscillator & Power
    // The StringConverter is required to make ObjectProperty work
    public StringConverter<Power> getConverter() {
        return new StringConverter<Power>() {

            @Override
            public Power fromString(String arg0) {
                return Power.valueOf(arg0);
            }

            @Override
            public String toString(Power arg0) {
                return arg0.name();
            }
        };
    }
}
