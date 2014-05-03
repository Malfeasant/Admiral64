package us.malfeasant.admiral64.vic;

class RegisterBank {
	final SpriteControl[] spriteControl;
	
	RegisterBank() {
		spriteControl = new SpriteControl[8];
		for (int i = 0; i < 8; ++i) {
			spriteControl[i] = new SpriteControl(i);
		}
	}
	
	int busCycle(int addr, int data, boolean read) {
		if (addr > 0x2e) return data;
		if (addr < 0x10) {	// sprite position
			SpriteControl sc = spriteControl[addr / 2];
			data = sc.pos(addr, data, read);
		} else if (addr >= 0x27) {	// sprite color
			SpriteControl sc = spriteControl[addr - 0x27];
			if (read) {
				data = sc.color | 0xf0;
			} else {
				sc.color = data & 0xf;
			}
		} else if (addr >= 0x20) {	// other color
			
		} else {	// anything else
			switch (addr) {
			case 0x10:
				for (int i = 0; i < 8; ++i) {
					data |= spriteControl[i].pos(addr, data, read);
				}
			}
		}
		return data;
	}
}
