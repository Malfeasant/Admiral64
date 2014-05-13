package us.malfeasant.admiral64.vic;

class CollideFlags extends SpriteFlags {
	CollideFlags(boolean[] flags) {
		super(flags);
	}
	@Override
	public int pack() {
		int data = super.pack();
		super.unpack(0);
		return data;
	}
	@Override
	public void unpack(int data) {}	// ignore write
}
