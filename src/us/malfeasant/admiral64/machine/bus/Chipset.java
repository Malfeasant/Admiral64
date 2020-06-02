package us.malfeasant.admiral64.machine.bus;

import us.malfeasant.admiral64.machine.cia.CIA;
import us.malfeasant.admiral64.machine.vic.Vic;

/**
 * A container for all the devices on the bus- put in one place to make interconnections easier
 * @author Malfeasant
 */
public class Chipset {
	public final Vic vic;
	public final CIA cia1;
	public final CIA cia2;
	public final ROM basic;
	public final ROM charGen;
	public final ROM kernal;
	
	public Chipset(Vic v, ROM b, ROM k, ROM ch, CIA c1, CIA c2 /* SID, RAM, anything else? */) {
		assert (v != null) : "Chipset constructor missing VIC";
		assert (b != null) : "Chipset constructor missing VIC";
		assert (k != null) : "Chipset constructor missing VIC";
		assert (ch != null) : "Chipset constructor missing VIC";
		assert (c1 != null) : "Chipset constructor missing VIC";
		assert (c2 != null) : "Chipset constructor missing VIC";
		vic = v;
		cia1 = c1;
		cia2 = c2;
		basic = b;
		charGen = ch;
		kernal = k;
	}
}
