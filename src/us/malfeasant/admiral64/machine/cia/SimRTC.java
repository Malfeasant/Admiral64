package us.malfeasant.admiral64.machine.cia;

/**
 * Fully simulated RTC- clock runs at simulation speed
 */
public class SimRTC extends RTC {
	private int time;	// packed bcd bits: pHhh hhMM Mmmm mSSS ssss tttt
	
	@Override
	public void tick() {
		// Break out individual fields and increment as needed- only unpack/pack fields that need to be modified
		// Weird increment behavior mimics the real thing- individual fields can be set out of range, then when
		// incremented, only carries to next stage if in range- out of range fields will wrap to 0 when they run
		// out of bits, but will not increment next stage.
		int ten = time & 0xf;
		if (ten == 9) {	// xx:xx:xx.9
			ten = 0;
			int sec = time & 0xf0;
			if (sec == 0x90) {	// xx:xx:x9.9
				sec = 0;
				int tsec = time & 0x700;
				if (tsec == 0x500) {	// xx:xx:59.9
					tsec = 0;
					int min = time & 0xe800;
					if (min == 0x8800) {	// xx:x9:59.9
						min = 0;
						int tmin = time & 0x38000;
						if (tmin == 0x28000) {	// xx:59:59.9
							tmin = 0;
							// now things get more complicated...
							int hour = time & 0x3c0000;
							int thour = time & 0x400000;
							int pm = time & 0x800000;
							// TODO: finish this
						} else {
							tmin += 0x8000;
						}
						time &= ~0x38000;
						time |= tmin & 0x38000;
					} else {
						min += 0x800;
					}
					time &= ~0xe800;
					time |= min & 0xe800;
				} else {
					tsec += 0x100;
				}
				time &= ~0x700;
				time |= tsec & 0x700;
			} else {
				sec += 0x10;
			}
			time &= ~0xf0;
			time |= sec & 0xf0;
		} else {
			ten++;
		}
		time &= ~0xf;
		time |= (ten & 0xf);
	}
	@Override
	public int getTime() {
		return time;
	}
}
