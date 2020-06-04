package us.malfeasant.admiral64.machine.bus;

/**
 * More than just the bus- really all shared signals that might be needed...
 * @author Malfeasant
 */
public class Bus {
	private boolean irq;
	private boolean nmi;
	
	private int ioport;	// 6510's 6-bit port, seen at location 1 (with direction an 0)
	private int iodir;	// direction register of 6-bit i/o port- 1 = output
/*	Bit 0: LORAM signal.  Selects ROM or RAM at 40960 ($A000).  1=BASIC, 0=RAM
	Bit 1: HIRAM signal.  Selects ROM or RAM at 57344 ($E000).  1=Kernal, 0=RAM
	Bit 2: CHAREN signal.  Selects character ROM or I/O devices.  1=I/O, 0=ROM
	Bit 3: Cassette Data Output line.
	Bit 4: Cassette Switch Sense.  Reads 0 if a button is pressed, 1 if not.
	Bit 5: Cassette Motor Switch Control.  A 1 turns the motor on, 0 turns it off.
	Bits 6-7: Not connected--no function presently defined.	 */
	
	private int vicBank;	// two bits from CIA#2's Port A supply upper two bits for VIC reads- inverted
	
	private final Chipset chips;
	
	public Bus(Chipset c) {
		assert (c != null) : "Bus requires a Chipset.";
		chips = c;
		chips.vic.connectBus(new VicBus());
		// TODO: same for CPU
	}
	
	/**
	 * Connects the VIC to the bus as a driver- only reads, and address decoding is different from the CPU's view
	 */
	public class VicBus {
		/**
		 * Normal read- used for sprite pointer/data accesses, and g-data- only happens when clock low. 
		 * @param addr address to read
		 * @return 8 bits of data
		 */
		public int read8(int addr) {
			return read(addr) & 0xff;
		}
		/**
		 * Wide read- used for character fetches.  BA & AEC should be low.  Only happens when clock high.
		 * @param addr address to read
		 * @return 12 bits- low 8 bits is char pointer, high 4 comes from color RAM 
		 */
		public int read12(int addr) {
			assert(!chips.vic.getAEC()) : "Cycle stolen without CPU stun.";
			return read(addr) & 0xfff;
		}
		private int read(int addr) {
			assert (addr == (addr & 0x3fff)) : "VIC set impossible address: " + addr;
			return 0;	// TODO: guts
		}
	}
	
	/**
	 * Connects the CPU to the bus
	 */
	public class CpuBus {
		public int read(int addr) {
			assert (addr == (addr & 0xffff)) : "CPU set impossible address: " + addr;
			return 0;	// TODO: guts
		}
		
		public void write(int addr, int data) {
			assert (addr == (addr & 0x3fff)) : "CPU set impossible address: " + addr;
			assert (data == (data & 0xff)) : "CPU set impossible data: " + data;
		}
	}
}
