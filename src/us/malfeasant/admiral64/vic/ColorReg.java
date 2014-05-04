package us.malfeasant.admiral64.vic;

class ColorReg {
	int color;
	
	void set(int c) {
		color = c & 0xf;
	}
	int get() {
		return color | 0xf0;
	}
}
