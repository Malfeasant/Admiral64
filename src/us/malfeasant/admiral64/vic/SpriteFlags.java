package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

class SpriteFlags implements Register {
	private final boolean[] flags;
	
	SpriteFlags(boolean[] flags) {
		this.flags = flags;
	}

	@Override
	public int pack() {
		int data = 0;
		for (int i = 0, w = 1; i < flags.length; ++i, w <<= 1) {
			if (flags[i]) data |= w;
		}
		return data;
	}

	@Override
	public void unpack(int data) {
		for (int i = 0, w = 1; i < flags.length; ++i, w <<= 1) {
			flags[i] = (data & w) != 0;
		}
	}
}
