package us.malfeasant.admiral64.machine;

import org.tinylog.Logger;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableBooleanValue;
import us.malfeasant.admiral64.configuration.Configuration;

/**
 * Holds everything needed to run a machine including its configuration, screen, keyboard...
 */
public class Machine {
    public final Configuration config;
    private final ReadOnlyBooleanWrapper runningWrapper = new ReadOnlyBooleanWrapper(false);
    public final ObservableBooleanValue runningProperty = runningWrapper.getReadOnlyProperty();

    public Machine(Configuration conf) {
        config = conf;
        Logger.info("Building new machine: {}", config);
    }

    /**
     * Power up this machine
     */
    public void start() {
        Logger.info("Starting machine {}", config);
    }

    /**
     * Suspend a running machine- snapshot the RAM, anything else?
     */
    public void freeze() {
        Logger.info("Freezing machine {}", config);
    }

    /**
     * Kill a running machine, akin to pulling the plug
     */
    public void stop() {
        Logger.info("Starting machine {}", config);
    }

    public String toString() {
        // TODO This is not ideal...  wouldn't be needed if we had a custom ListCell...
        return config.toString();
    }
}
