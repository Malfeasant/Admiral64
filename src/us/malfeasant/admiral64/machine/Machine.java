package us.malfeasant.admiral64.machine;

import java.io.IOException;

import us.malfeasant.admiral64.Configuration;
import us.malfeasant.admiral64.console.FrameBuffer;
import us.malfeasant.admiral64.machine.vic.Vic;
import us.malfeasant.admiral64.timing.CrystalConsumer;
import us.malfeasant.admiral64.timing.TimingGenerator;
import us.malfeasant.admiral64.machine.bus.Bus;
import us.malfeasant.admiral64.machine.bus.Chipset;
import us.malfeasant.admiral64.machine.bus.ROM;
import us.malfeasant.admiral64.machine.cia.CIA;

/**
 *	This class will encompass the entire simulation, minus the gui bits and thread management.  Ideally, it shouldn't
 *	care what thread runs it, assuming it's one, and shouldn't care whether its gui is JavaFX or Swing or something else.
 */
public class Machine {
	private final Chipset chips;
	
	public Machine(Configuration conf) {
		Vic vic = new Vic(conf.vicFlavor);
		CIA cia1 = new CIA(conf.rtcMode1);
		CIA cia2 = new CIA(conf.rtcMode2);
		ROM basic = null;
		ROM charGen = null;
		ROM kernal = null;
		
		try {
			basic = conf.basicRom.load();
			charGen = conf.charRom.load();
			kernal = conf.kernalRom.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("One of the built-in ROMs failed to load- this should not happen.", e);
			// TODO: catch and do something sensible.  For now, just die.
		}
		chips = new Chipset(vic, basic, kernal, charGen, cia1, cia2);
		new Bus(chips);
	}
	
	public void connectTiming(TimingGenerator tg) {
		tg.addCrystalConsumer(chips.cia1);
		tg.addCrystalConsumer(chips.cia2);
		//tg.addCrystalConsumer(chips.vic);	// Need to proxy Vic & CPU to allow vic's flags to affect cpu...
		tg.addCrystalConsumer(new CrystalConsumer() {
			@Override
			public void cycle() {
				chips.vic.cycle();
				boolean rdy = chips.vic.getBA();
				boolean aec = chips.vic.getAEC();
				// TODO: set these flags in CPU, cycle it
			}
		});
		tg.addPowerConsumer(chips.cia1);
		tg.addPowerConsumer(chips.cia2);
	}
	
	public FrameBuffer getFrameBuffer() {
		return chips.vic.getFrameBuffer();
	}
}
