package us.malfeasant.admiral64.configuration;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ConfigurationBuilder {
    public final StringProperty nameProperty;
    public final ObjectProperty<Oscillator> oscillatorProperty;
    public final ObjectProperty<Power> powerProperty;
    public final ObjectProperty<TimeSource> timeSourceProperty;

    public ConfigurationBuilder() {
        nameProperty = new SimpleStringProperty("");
        oscillatorProperty = new SimpleObjectProperty<Oscillator>(Oscillator.NTSC);
        powerProperty = new SimpleObjectProperty<Power>(Power.NA);
        timeSourceProperty = new SimpleObjectProperty<TimeSource>(TimeSource.HOST);
    }

    public Configuration makeFrom() {
        return new Configuration(
            nameProperty.get(),
            oscillatorProperty.get(),
            powerProperty.get(),
            timeSourceProperty.get()
        );
    }
}
