package us.malfeasant.admiral64.machine.bus;

/**
 *	Kernal/Basic/Char ROMs will use this to load their contents, as well as possible cartridge ROMs.
 */
public class ROM implements Peekable {
	private final byte[] bytes;
	private final int addrMask;
	
	protected ROM(int addrWidth, byte[] contents) {
		assert contents.length == (2 ^ addrWidth) : "ROM: Address bus width does not match content length.";
		addrMask = 2 ^ addrWidth - 1;
		bytes = contents;
	}
	
	@Override
	public int peek(int addr) {
		assert addr == (addr & addrMask) : "ROM: Address out of range.";
		addr &= addrMask;
		return bytes[addr];
	}
}
