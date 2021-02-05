package us.malfeasant.admiral64.machine.cia;

import us.malfeasant.admiral64.machine.bus.Peekable;
import us.malfeasant.admiral64.machine.bus.Pokeable;
import us.malfeasant.admiral64.machine.cia.RTC.Mode;
import us.malfeasant.admiral64.timing.CrystalConsumer;
import us.malfeasant.admiral64.timing.PowerConsumer;

public class CIA implements CrystalConsumer, PowerConsumer, Peekable, Pokeable {
	private final RTC rtc;
	
	private int ticks;
	private int tickDiv = 6;	// There were no 2 different parts for EU/NA- all CIAs default to 60Hz power
	// must be changed in software for 50Hz.
	
	public CIA(Mode mode) {
		rtc = mode.factory.get();
	}
	
	/**
	 *	Tick the RTC
	 */
	@Override
	public void tick() {
		ticks = (ticks + 1) % tickDiv;
		if (ticks == 0) rtc.tick();	// TODO: find out what happened in a real machine
		// if power freq divider was changed when tick was already out of range-
		// very small chance of it happpening, but possible...
		// go to 0 from 7?  go to 0 immediately?
	}

	@Override
	public void negEdge() {
		// TODO implement timer logic - most likely only need to react to a single edge, but who knows...
	}

	@Override
	public void posEdge() {
		// TODO implement timer logic - most likely only need to react to a single edge, but who knows...
	}
	
	@Override
	public void poke(int addr, int data) {
		// TODO: implement registers
	}

	@Override
	public int peek(int addr) {
		// TODO: implement registers
		return 0;
	}

}
