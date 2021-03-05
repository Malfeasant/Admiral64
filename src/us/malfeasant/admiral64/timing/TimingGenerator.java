package us.malfeasant.admiral64.timing;

import java.util.concurrent.CopyOnWriteArraySet;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import us.malfeasant.admiral64.worker.WorkQueue;

/**
 *	generates the timing events that keep the machine running.  Minimum options should be single CPU cycle,  
 *	realtime, or uninhibited. 
 */
public class TimingGenerator {
	private int cyclesSinceTick;	// so even if single stepping, ticks happen in the right places
	private int tickRemainder;	// since nothing is even multiples
	private long targetTime = System.currentTimeMillis();	// used for realtime calculation- timestamp of next iteration
	private int ticksSincePause;	// used for realtime calculation- inefficient (and more remainders!) to pause every tick
	
	private final CopyOnWriteArraySet<PowerConsumer> powerConsumers;
	private final CopyOnWriteArraySet<CrystalConsumer> crystalConsumers;
	
	private final HBox buttons = new HBox();
	private final Oscillator osc;
	private final Powerline pow;
	private final int cyclesPerTick;
	private final int cyclesPerTickDiv;
	private final int cyclesPerTickRem;
	private final int ticksPerPause;
	private final int pauseTime;
	
	public TimingGenerator(Oscillator o, Powerline p, WorkQueue.WorkSender s) {
		powerConsumers = new CopyOnWriteArraySet<PowerConsumer>();
		crystalConsumers = new CopyOnWriteArraySet<CrystalConsumer>();
		osc = o;
		pow = p;
		
		ticksPerPause = pow == Powerline.NA ? 3 : 2;	// in realtime, pause every 1/20th second for NA, 1/25th for EU
		pauseTime = ticksPerPause * 1000 / pow.cycles;	// ends up being time in ms to run that many ticks
		cyclesPerTickDiv = osc.seconds * pow.cycles;	// NTSC 660, PAL 900 (unless power mismatched, then NTSC 550, PAL 1080)
		cyclesPerTick = osc.cycles / cyclesPerTickDiv;	// NTSC 17045, PAL 19704 (assuming matching powerline)
		cyclesPerTickRem = osc.cycles % cyclesPerTickDiv;	// NTSC 300, PAL 875
		
		for (RunMode mode : RunMode.values()) {
			buttons.getChildren().add(mode.makeButton(e -> {
				s.changeMode(mode);
			}));
		}
		buttons.setAlignment(Pos.CENTER);
	}
	
	// this will be called on the worker thread
	public void run(RunMode mode) {
		int cycles = mode == RunMode.STEP ? 1 : cyclesPerTick - cyclesSinceTick;
		for (int cycle = 0; cycle < cycles; cycle++) {
			for (CrystalConsumer cc : crystalConsumers) {
				cc.negEdge();
				cc.posEdge();
			}
		}
		
		cyclesSinceTick += cycles;	// record the number of cycles just run
		
		if (cyclesSinceTick >= cyclesPerTick) {
			tickRemainder += cyclesPerTickRem;
			int fudge = tickRemainder / cyclesPerTickDiv;	// represents extra cycles that need to be run
			tickRemainder %= cyclesPerTickDiv;
			cyclesSinceTick -= cyclesPerTick + fudge;	// if fudge is 1, this leaves cyclesSinceTick at -1, so an extra cycle will be run next time
			
			for (PowerConsumer pc : powerConsumers) {
				pc.tick();
			}
			if (mode == RunMode.REAL) ticksSincePause++;
		}
		
		if (mode == RunMode.REAL && ticksSincePause >= ticksPerPause) {
			// Figure out how long to sleep
			targetTime += pauseTime;
			long now = System.currentTimeMillis();
			long diff = targetTime - now;	// difference between 1/10th second and next run
			ticksSincePause = 0;
			if (diff > 0) {	// if something bogs down the simulation, this will go negative- should catch up before too long?
				try {
					Thread.sleep(diff);
				} catch (InterruptedException e) {}
			}
		}
	}
	
	// Threadsafe add listener for cpu clock events
	public void addCrystalConsumer(CrystalConsumer cc) {
		crystalConsumers.add(cc);
	}
	public void removeCrystalConsumer(CrystalConsumer cc) {
		crystalConsumers.remove(cc);
	}
	
	// Threadsafe add listener for powerline events
	public void addPowerConsumer(PowerConsumer pc) {
		powerConsumers.add(pc);
	}
	public void removePowerConsumer(PowerConsumer pc) {
		powerConsumers.remove(pc);
	}
	
	public Node getButtons() {
		return buttons;
	}
}
