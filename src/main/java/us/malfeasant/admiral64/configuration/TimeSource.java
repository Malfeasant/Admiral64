package us.malfeasant.admiral64.configuration;

/**
 * Configuration option- how the ToD sections of the CIA chips should simulate
 * the passage of time.  Values are:
 * SIM - passage of time is fixed to simulation time- x jiffies per y oscillator cycles.
 * If simulation is paused, slowed, sped up, so is the simulated passage of time.
 * HOST - Passage of time is not simulated, ToD module has no internal functionality beyond
 * latching reads, which returns localtime as seen by the host.  Writing is a NoOp.
 * Each CIA has its own ToD module, and these can be set independently- perfectly cromulent
 * to use one of each.
 */
public enum TimeSource {
    SIM, HOST;
}
