package us.malfeasant.admiral64.vic;

class ColorReg extends IntRegister {
	void unpack(int c) {
		super.unpack(c & 0xf);
	}
	int pack() {
		return super.pack() | ~0xf;
	}
}
