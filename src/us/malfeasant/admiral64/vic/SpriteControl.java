package us.malfeasant.admiral64.vic;

class SpriteControl {
	boolean enable;
	boolean xExpand;
	boolean yExpand;
	boolean dataPriority;
	boolean multicolor;
	boolean collideSprite;
	boolean collideData;
	int color;
	int xPos;
	int yPos;
	private final int weight;
	
	SpriteControl(int which) {
		weight = 1 << which;
	}
	int pos(int a, int d, boolean read) {
		if (a == 0x10) {	// MSB
			if (read) {
				d = (xPos & 0x100) == 0 ? 0 : weight;
			} else {
				if ((weight & d) == 0) {
					xPos &= 0xff;
				} else {
					xPos |= 0x100;
				}
			}
		} else {
			if ((a & 1) == 0) {
				if (read) {
					d = xPos & 0xff;
				} else {
					xPos &= 0x100;
					xPos |= d;
				}
			} else {
				if (read) {
					d = yPos;
				} else {
					yPos = d;
				}
			}
		}
		return d;
	}
}
