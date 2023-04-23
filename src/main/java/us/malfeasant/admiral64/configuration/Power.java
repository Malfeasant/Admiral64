package us.malfeasant.admiral64.configuration;

public enum Power {
    NA(6), EU(5);

    private final int jiffiesPerTick;
    private final String hz;
    Power(int j) {
        jiffiesPerTick = j;
        hz = String.format("%dHz", jiffiesPerTick * 10);
    }
    @Override
    public String toString() {
        return hz;
    }
}
