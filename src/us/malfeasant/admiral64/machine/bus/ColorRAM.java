package us.malfeasant.admiral64.machine.bus;

public class ColorRAM implements Peekable, Pokeable {
	private final byte[] ram = new byte[0x400];	// not going to do anything sneaky like pack 2 nybbles per byte...
	
	@Override
	public void poke(int addr, int data) {
		ram[addr] = (byte) (data & 0xf);
	}

	@Override
	public int peek(int addr) {
		return ram[addr] | 0xf0;	// reads of real color ram set these bits high
	}
}
