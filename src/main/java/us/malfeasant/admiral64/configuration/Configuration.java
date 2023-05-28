package us.malfeasant.admiral64.configuration;

import org.tinylog.Logger;

/**
 * Houses all the configuration bits that are *not* expected to change- chipset, pal vs ntsc,
 * chip revisions, etc.  What should *not* be in here is stuff that is trivial to change while
 * the machine is running- disk images, simulation parameters...
 * So for example, whether the CIA chips' ToD clock is simulated or returns real time should
 * not be in here. (yes I started to, then deciced against it) Now, should 60 vs 50 Hz be here?
 * Leaving it for the moment, but since there is no actual difference to the machine, maybe not?
 */
public class Configuration {
    public final String name;
    public final Oscillator oscillator;
    public final Power power;

    Configuration(String name, Oscillator osc, Power pow) {
        if (name == null || osc == null || pow == null) {
            Logger.error(new IllegalArgumentException("Null(s) in Configuration constructor!"));
        }
        this.name = name;
        oscillator = osc;
        power = pow;
    }

    @Override
    public String toString() {
        return "Configuration: " + cellLabel();
    }
    public String cellLabel() {
        return name + ": " + oscillator + ", " + power;
    }
}
