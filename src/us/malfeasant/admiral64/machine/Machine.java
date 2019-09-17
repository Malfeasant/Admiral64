package us.malfeasant.admiral64.machine;

import java.io.IOException;

import us.malfeasant.admiral64.Configuration;
import us.malfeasant.admiral64.console.FrameBuffer;
import us.malfeasant.admiral64.machine.vic.Vic;
import us.malfeasant.admiral64.timing.TimingGenerator;
import us.malfeasant.admiral64.machine.bus.ROM;
import us.malfeasant.admiral64.machine.cia.CIA;

/**
 *	This class will encompass the entire simulation, minus the gui bits and thread management.  Ideally, it shouldn't
 *	care what thread runs it, assuming it's one, and shouldn't care whether its gui is JavaFX or Swing or something else.
 */
public class Machine {
	private final Vic vic;
	private final CIA cia1;
	private final CIA cia2;
	private final ROM basic;
	private final ROM charGen;
	private final ROM kernal;
	
	public Machine(Configuration conf) {
		vic = new Vic(conf.vicFlavor);
		cia1 = new CIA(conf.rtcMode1);
		cia2 = new CIA(conf.rtcMode2);
		try {
			basic = conf.basicRom.load();
			charGen = conf.charRom.load();
			kernal = conf.kernalRom.load();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("One of the built-in ROMs failed to load- this should not happen.", e);
			// TODO: catch and do something sensible.  For now, just die.
		}
	}
	
	public void connectTiming(TimingGenerator tg) {
		tg.addCrystalConsumer(cia1);
		tg.addCrystalConsumer(cia2);
		tg.addCrystalConsumer(vic);
		tg.addPowerConsumer(cia1);
		tg.addPowerConsumer(cia2);
	}
	
	public FrameBuffer getFrameBuffer() {
		return vic.getFrameBuffer();
	}
}
