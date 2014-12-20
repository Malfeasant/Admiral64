package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

class SpriteY implements Register {
	private final int[] array;
	private final int position;
	
	SpriteY(int[] a, int pos) {
		array = a;
		position = pos;
	}
	
	@Override
	public void unpack(int data) {
		array[position] = data & 0xff;
	}
	@Override
	public int pack() {
		return array[position];
	}
}