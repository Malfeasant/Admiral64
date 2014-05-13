package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.Register;

class SpriteWrangler {
	static final int SPRITES = 8;
	
	private final int[] xs = new int[SPRITES];
	private final int[] ys = new int[SPRITES];
	private final boolean[] enables = new boolean[SPRITES];
	private final boolean[] multicolors = new boolean[SPRITES];
	private final boolean[] expandXs = new boolean[SPRITES];
	private final boolean[] expandYs = new boolean[SPRITES];
	private final boolean[] dataPrioritys = new boolean[SPRITES];
	private final boolean[] collideSprites = new boolean[SPRITES];
	private final boolean[] collideDatas = new boolean[SPRITES];
	private final byte[] colors = new byte[SPRITES];
	
	Register[] xLo = new Register[SPRITES];
	Register[] y = new Register[SPRITES];
	Register xHi = new Register() {
		@Override
		public void unpack(int data) {
			for (int i = 0, w = 1; i < SPRITES; ++i, w <<= 1) {
				if ((w & data) != 0) {
					xs[i * 2] |= 0x100;
				} else {
					xs[i * 2] &= 0xff;
				}
			}
		}
		@Override
		public int pack() {
			int data = 0;
			for (int i = 0, w = 1; i < SPRITES; ++i, w <<= 1) {
				if (xs[i * 2] > 0xff) data |= w;
			}
			return data;
		}
	};
	Register enable = new SpriteFlags(enables);
	Register multicolor = new SpriteFlags(multicolors);
	Register expandX = new SpriteFlags(expandXs);
	Register expandY = new SpriteFlags(expandYs);
	Register dataPriority = new SpriteFlags(dataPrioritys);
	Register collideSprite = new CollideFlags(collideSprites);
	Register collideData = new CollideFlags(collideDatas);
	Register[] color = new Register[SPRITES];
	
	SpriteWrangler() {
		for (int i = 0; i < SPRITES; ++i) {
			xLo[i] = new SpriteX(xs, i);
			y[i] = new SpriteY(ys, i);
			color[i] = new ColorRegister(colors, i);
		}
	}
	void setCollideSprite(int which) {
		collideSprites[which] = true;
	}
	void setCollideData(int which) {
		collideDatas[which] = true;
	}
}
