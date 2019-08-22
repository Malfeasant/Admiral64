package us.malfeasant.admiral64.machine.cia;

/**
 * Fully simulated RTC- clock runs at simulation speed
 */
public class SimRTC extends RTC {
	
	private int time;	// packed bcd bits: phhhhhmmmmmmmssssssstttt
	@Override
	public void tick() {
		
	}
	@Override
	public int getTime() {
		// TODO Auto-generated method stub
		return 0;
	}
}
