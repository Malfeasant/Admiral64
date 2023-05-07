package us.malfeasant.admiral64.machine;

import org.tinylog.Logger;

import javafx.animation.AnimationTimer;
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
    private final AnimationTimer timer;

    public ReadOnlyBooleanProperty runningProperty() {
        return runningProperty;
    }
    public Machine(Configuration conf) {
        config = conf;
        Logger.info("Building new machine: {}", config);
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                handleTimer(now);
            }
        };
    }

    private void handleTimer(long now) {

    }

    /**
     * Power up this machine
     */
    public void start() {
        Logger.info("Starting machine {}", config);
        timer.start();
        runningWrapper.set(true);
    }

    /**
     * Suspend a running machine- snapshot the RAM, anything else?
     */
    public void freeze() {
        Logger.info("Freezing machine {}", config);
        timer.stop();
        runningWrapper.set(false);
    }

    /**
     * Resume a suspended machine
     */
    public void thaw() {
        Logger.info("Thawing machine {}", config);
        timer.start();
        runningWrapper.set(true);
    }

    /**
     * Kill a running machine, akin to pulling the plug
     */
    public void stop() {
        Logger.info("Stopping machine {}", config);
        timer.stop();
        runningWrapper.set(false);
    }
}
