package us.malfeasant.admiral64.configuration;

import javafx.util.StringConverter;

public enum Power {
    NA, EU;

    // TODO find a way to generalize this for both Oscillator & Power
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
