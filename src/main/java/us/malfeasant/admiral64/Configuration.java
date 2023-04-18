package us.malfeasant.admiral64;

public class Configuration {
    private final String name;

    // Messy constructor is private- factory method will build a default configuration, then changes will create
    // new object, calling messy constructor only to change one thing at a time.
    private Configuration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Configuration getDefault(String name) {
        if (name == null) throw new IllegalArgumentException("Must supply a name.");
        return new Configuration(name);
    }

    // TODO more to come...
}
