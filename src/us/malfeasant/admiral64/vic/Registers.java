package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

public class Registers {
	private static final int SPRITES = 8;
	private static final int REGCOUNT = 47;
	private static final Register DEAD = new Register() {
		@Override
		public void unpack(int data) {}
		@Override
		public int pack() {
			return -1;
		}
	};
	private final Register[] busView = new Register[REGCOUNT];
	private final byte[] colors = new byte[7];
	
	private final byte[] spriteColors = new byte[SPRITES];
	private final int[] spriteX = new int[SPRITES];
	private final int[] spriteY = new int[SPRITES];
	private final boolean[] spriteEn = new boolean[SPRITES];
	private final boolean[] spriteMC = new boolean[SPRITES];
	private final boolean[] spriteExpandX = new boolean[SPRITES];
	private final boolean[] spriteExpandY = new boolean[SPRITES];
	private final boolean[] spriteDataPriority = new boolean[SPRITES];
	private final boolean[] spriteCollideSprite = new boolean[SPRITES];
	private final boolean[] spriteCollideData = new boolean[SPRITES];
	
	private int rasterCompare;
	private int rasterCurrent;
	private int scrollX;
	private int scrollY;
	private boolean bitmap;
	private boolean multiColor;
	private boolean extColor;
	private boolean displayEnable;
	private boolean wide;
	private boolean tall;
	private int lightPenX;
	private int lightPenY;
	
	public Registers() {
		for (int i = 0; i < SPRITES; ++i) {
			busView[0x27 + i] = new ColorRegister(spriteColors, i);
			busView[i * 2] = new SpriteX(spriteX, i);
			busView[i * 2 + 1] = new SpriteY(spriteY, i);
		}
		busView[0x10] = new Register() {
			@Override
			public void unpack(int data) {
				for (int i = 0, w = 1; i < SPRITES; ++i, w <<= 1) {
					if ((w & data) != 0) {
						spriteX[i * 2] |= 0x100;
					} else {
						spriteX[i * 2] &= 0xff;
					}
				}
			}
			@Override
			public int pack() {
				int data = 0;
				for (int i = 0, w = 1; i < SPRITES; ++i, w <<= 1) {
					if (spriteX[i * 2] > 0xff) data |= w;
				}
				return data;
			}
		};
		busView[0x11] = new Register() {
			@Override
			public void unpack(int data) {
				if ((data & 0x80) != 0) {
					rasterCompare |= 0x100;
				} else {
					rasterCompare &= 0xff;
				}
				extColor = (data & 0x40) != 0;
				bitmap = (data & 0x20) != 0;
				displayEnable = (data & 0x10) != 0;
				tall = (data & 8) != 0;
				scrollY = data & 7;
			}
			@Override
			public int pack() {
				int data = scrollY;
				if (tall) data |= 8;
				if (displayEnable) data |= 0x10;
				if (bitmap) data |= 0x20;
				if (extColor) data |= 0x40;
				if (rasterCurrent > 0xff) data |= 0x80;
				return data;
			}
		};
		busView[0x12] = new Register() {
			@Override
			public void unpack(int data) {
				data &= 0xff;
				rasterCompare &= 0x100;
				rasterCompare |= data;
			}
			@Override
			public int pack() {
				return rasterCurrent & 0xff;
			}
		};
		busView[0x13] = new Register() {
			@Override
			public void unpack(int data) {
				lightPenX = data & 0xff;
			}
			
			@Override
			public int pack() {
				return lightPenX;
			}
		};
		busView[0x14] = new Register() {
			@Override
			public void unpack(int data) {
				lightPenY = data & 0xff;
			}
			
			@Override
			public int pack() {
				return lightPenY;
			}
		};
		busView[0x15] = new SpriteFlags(spriteEn);
		busView[0x17] = new SpriteFlags(spriteExpandY);
		busView[0x1b] = new SpriteFlags(spriteDataPriority);
		busView[0x1c] = new SpriteFlags(spriteMC);
		busView[0x1d] = new SpriteFlags(spriteExpandX);
		busView[0x1e] = new SpriteFlags(spriteCollideSprite);
		busView[0x1f] = new SpriteFlags(spriteCollideData);
		for (int i = 0; i < colors.length; ++i) {
			busView[0x20 + i] = new ColorRegister(colors, i);
		}
	}
	Register select(int addr) {
		return addr > REGCOUNT ? DEAD : busView[addr];
	}
	boolean rasterMatch(int now) {
		return now == rasterCurrent;
	}
}
