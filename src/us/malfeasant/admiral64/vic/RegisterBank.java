package us.malfeasant.admiral64.vic;

class RegisterBank {
	final SpriteWrangler sprites;
	final ColorReg[] colors = new ColorReg[7];
	int rasterComp;
	int raster;
	boolean extColor;
	boolean bitmap;
	boolean displayEn;
	boolean allRows;
	int yScroll;
	int lpx;
	int lpy;
	boolean res;
	
	RegisterBank() {
		sprites = new SpriteWrangler();
		for (int i = 0; i < 8; ++i) {
			colors[i] = new ColorReg();
		}
	}
	
	int busCycle(int addr, int data, boolean read) {
		if (addr < 0x10) {	// sprite position
			int sprite = addr / 2;
			boolean even = (addr & 1) == 0;
			if (read) {
				data = even ? sprites.getLowX(sprite) : sprites.getY(sprite);
			} else {
				if (even) {
					sprites.setLowX(sprite, data);
				} else {
					sprites.setY(sprite, data);
				}
			}
		} else if (addr >= 0x27) {	// sprite color
			int sprite = addr - 0x27;
			if (read) {
				data = sprites.getColor(sprite) | 0xf0;
			} else {
				sprites.setColor(sprite, data & 0xf);
			}
		} else if (addr >= 0x20) {	// other color
			int col = addr - 0x20;
			if (read) {
				data = colors[col].pack();
			} else {
				colors[col].unpack(data);
			}
		} else {	// anything else
			switch (addr) {
			case 0x10:	// sprite x MSBs
				if (read) {
					data = sprites.getHighX();
				} else {
					sprites.setHighX(data);
				}
				break;
			case 0x11:	// Control reg 1
				break;
			case 0x12:	// raster lsb
				break;
			case 0x13:	// light pen x
				break;
			case 0x14:	// light pen y
				break;
			case 0x15:	// sprite enable
				if (read) {
					data = sprites.getEnable();
				} else {
					sprites.setEnable(data);
				}
				break;
			case 0x16:	// control reg 2
				break;
			case 0x17:	// sprite y expand
				if (read) {
					data = sprites.getExpandY();
				} else {
					sprites.setExpandY(data);
				}
				break;
			case 0x18:	// vm/cb
				break;
			case 0x19:	// irq
				break;
			case 0x1a:	// irq en
				break;
			case 0x1b:	// sprite data priority
				if (read) {
					data = sprites.getDataPri();
				} else {
					sprites.setDataPri(data);
				}
				break;
			case 0x1c:	// sprite multicolor enable
				if (read) {
					data = sprites.getEnableMC();
				} else {
					sprites.setEnableMC(data);
				}
				break;
			case 0x1d:	// sprite x expand
				if (read) {
					data = sprites.getExpandX();
				} else {
					sprites.setExpandX(data);
				}
				break;
			case 0x1e:	// sprite-sprite collision
				if (read) {
					data = sprites.getCollideS();
				}	// no else- write has no effect
				break;
			case 0x1f:	// sprite-data collision
				if (read) {
					data = sprites.getCollideD();
				}	// no else- write has no effect
				break;
			}
		}
		return data;
	}
}
