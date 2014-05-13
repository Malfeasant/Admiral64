package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

class InterruptWrangler {
	private boolean irq;
	private boolean raster;
	private boolean enableRaster;
	private boolean spriteCollision;
	private boolean enableSpriteCollision;
	private boolean dataCollision;
	private boolean enableDataCollision;
	private boolean lightPen;
	private boolean enableLightPen;
	
	Register latch = new Register() {
		@Override
		public void unpack(int data) {
			boolean sync = false;
			if ((data & 8) != 0) {
				lightPen = false;
				sync = true;
			}
			if ((data & 4) != 0) {
				spriteCollision = false;
				sync = true;
			}
			if ((data & 2) != 0) {
				dataCollision = false;
				sync = true;
			}
			if ((data & 1) != 0) {
				raster = false;
				sync = true;
			}
			if (sync) sync();
		}
		@Override
		public int pack() {
			int data = 0x70;	// dead bits
			if (irq) data |= 0x80;
			if (lightPen) data |= 8;
			if (spriteCollision) data |= 4;
			if (dataCollision) data |= 2;
			if (raster) data |= 1;
			return data;
		}
	};
	Register enable = new Register() {
		@Override
		public void unpack(int data) {
			enableLightPen = (data & 8) != 0;
			enableSpriteCollision = (data & 4) != 0;
			enableDataCollision = (data & 2) != 0;
			enableRaster = (data & 1) != 0;
		}
		@Override
		public int pack() {
			int data = 0xf0;
			if (enableLightPen) data |= 8;
			if (enableSpriteCollision) data |= 4;
			if (enableDataCollision) data |= 2;
			if (enableRaster) data |= 1;
			return data;
		}
	};
	void setRaster() {
		raster = true;
		sync();
	}
	void setLightPen() {
		lightPen = true;
		sync();
	}
	void setDataCollision() {
		dataCollision = true;
		sync();
	}
	void setSpriteCollision() {
		spriteCollision = true;
		sync();
	}
	private void sync() {
		irq = (raster & enableRaster) | (spriteCollision & enableSpriteCollision) |
				(dataCollision & enableDataCollision) | (lightPen & enableLightPen);
	}
}
