package us.malfeasant.admiral64.configuration;

import org.tinylog.Logger;

public class Configuration {
    public final Oscillator oscillator;
    public final Power power;

    Configuration(Oscillator osc, Power pow) {
        if (osc == null || pow == null) {
            Logger.error(new IllegalArgumentException("Nulls in Configuration!"));
        }
        oscillator = osc;
        power = pow;
    }
}
