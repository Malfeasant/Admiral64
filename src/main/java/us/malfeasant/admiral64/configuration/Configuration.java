package us.malfeasant.admiral64.configuration;

import org.tinylog.Logger;

public class Configuration {
    public final String name;
    public final Oscillator oscillator;
    public final Power power;

    Configuration(String name, Oscillator osc, Power pow) {
        if (name == null || osc == null || pow == null) {
            Logger.error(new IllegalArgumentException("Nulls in Configuration constructor!"));
        }
        this.name = name;
        oscillator = osc;
        power = pow;
    }

    public String cellLabel() {
        return name + ": " + oscillator + ", " + power;
    }
}
