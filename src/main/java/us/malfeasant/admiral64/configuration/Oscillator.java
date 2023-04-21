package us.malfeasant.admiral64.configuration;

import javafx.util.StringConverter;

public enum Oscillator {
    NTSC, PAL;

    // TODO find a way to generalize this for both Oscillator & Power
    // The StringConverter is required to make ObjectProperty work
    public StringConverter<Oscillator> getConverter() {
        return new StringConverter<Oscillator>() {

            @Override
            public Oscillator fromString(String arg0) {
                return Oscillator.valueOf(arg0);
            }

            @Override
            public String toString(Oscillator arg0) {
                return arg0.name();
            }
        };
    }
}
