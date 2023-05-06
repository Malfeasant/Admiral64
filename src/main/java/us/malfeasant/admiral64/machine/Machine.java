package us.malfeasant.admiral64.machine;

import org.tinylog.Logger;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import us.malfeasant.admiral64.configuration.Configuration;

/**
 * Holds everything needed to run a machine including its configuration, screen, keyboard...
 */
public class Machine {
    public final Configuration config;
    private final ReadOnlyBooleanWrapper runningWrapper = new ReadOnlyBooleanWrapper(false);
    private final ReadOnlyBooleanProperty runningProperty = runningWrapper.getReadOnlyProperty();

    public ReadOnlyBooleanProperty runningProperty() {
        return runningProperty;
    }
    public Machine(Configuration conf) {
        config = conf;
        Logger.info("Building new machine: {}", config);
    }

    /**
     * Power up this machine
     */
    public void start() {
        Logger.info("Starting machine {}", config);
        runningWrapper.set(true);
    }

    /**
     * Suspend a running machine- snapshot the RAM, anything else?
     */
    public void freeze() {
        Logger.info("Freezing machine {}", config);
        runningWrapper.set(false);
    }

    /**
     * Kill a running machine, akin to pulling the plug
     */
    public void stop() {
        Logger.info("Stopping machine {}", config);
        runningWrapper.set(false);
    }
}
