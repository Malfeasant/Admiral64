package us.malfeasant.admiral64.configuration;

import org.tinylog.Logger;

/**
 * Houses all the configuration bits that are *not* expected to change- chipset, pal vs ntsc,
 * chip revisions, etc.  What should *not* be in here is stuff that is trivial to change while
 * the machine is running- disk images, simulation parameters...
 * I keep going back and forth over whether the CIA chips' ToD clock is simulated or returns real
 * time should or should not be in here.  I think I'm going to settle on yes- because it's 
 * essentially hardware configuration, either real or fake.
 * Related to that- CIA1's ToD will always be simulated, and CIA2's configurable, since BASIC uses
 * CIA1 (for RND(0) function), seems like its behavior should be predictable.  
 * Now, should 60 vs 50 Hz be here?
 * Leaving it for the moment, but since there is no actual difference to the machine, maybe not?
 */
public class Configuration {
    public final String name;
    public final Oscillator oscillator;
    public final Power power;
    public final TimeSource timeSource;

    Configuration(String name, Oscillator osc, Power pow, TimeSource ts) {
        if (name == null || osc == null || pow == null || ts == null) {
            Logger.error(new IllegalArgumentException("Null(s) in Configuration constructor!"));
        }
        this.name = name;
        oscillator = osc;
        power = pow;
        timeSource = ts;
    }

    @Override
    public String toString() {
        return "Configuration: " + cellLabel();
    }
    public String cellLabel() {
        return name + ": " + oscillator + ", " + power + ", " + timeSource;
    }
}
