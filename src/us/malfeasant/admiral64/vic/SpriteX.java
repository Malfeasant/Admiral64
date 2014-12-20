package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

class SpriteX implements Register {
	private final int[] array;
	private final int position;
	
	SpriteX(int[] a, int pos) {
		array = a;
		position = pos;
	}
	
	@Override
	public int pack() {
		return array[position] & 0xff;
	}

	@Override
	public void unpack(int data) {
		data &= 0xff;
		array[position] &= 0x100;
		array[position] |= data;
	}
}
