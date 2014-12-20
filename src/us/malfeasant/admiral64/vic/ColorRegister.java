package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

class ColorRegister implements Register {
	private final byte[] array;
	private final int position;
	
	ColorRegister(byte[] array, int position) {
		this.array = array;
		this.position = position;
	}

	@Override
	public int pack() {
		return array[position] | 0xf0;
	}

	@Override
	public void unpack(int data) {
		array[position] = (byte)(data & 0xf);
	}
}
