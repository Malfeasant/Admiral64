package us.malfeasant.admiral64.machine;

/**
 * Simulation speed- Step runs a single CPU cycle- Turbo runs as fast as it can.
 * Real runs in realtime.  Slow and Fast run some fraction/multiple of realtime...
 */
public enum RunMode {
    STEP, SLOW, REAL, FAST, TURBO;
}
