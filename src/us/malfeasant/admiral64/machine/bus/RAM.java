package us.malfeasant.admiral64.machine.bus;

public class RAM implements Peekable, Pokeable {
	private final byte[] ram = new byte[0x10000];
	
	@Override
	public void poke(int addr, int data) {
		assert ((data & 0xff) == data) : "Ram set with impossible data.";
		ram[addr] = (byte) data;
	}
	
	@Override
	public int peek(int addr) {
		return ram[addr];	// TODO: do I need an & 0xff to undo sign extension?
	}
}
