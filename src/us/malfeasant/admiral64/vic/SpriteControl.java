package us.malfeasant.admiral64.vic;

class SpriteControl {
	boolean enable;
	boolean xExpand;
	boolean yExpand;
	boolean dataPriority;
	boolean multicolor;
	boolean collideSprite;
	boolean collideData;
	final ColorReg color;
	int xPos;
	int yPos;
	final int weight;
	
	SpriteControl(int which) {
		weight = 1 << which;
		color = new ColorReg();
	}
	
	boolean getCollideS() {
		boolean b = collideSprite;
		collideSprite = false;
		return b;
	}
	boolean getCollideD() {
		boolean b = collideData;
		collideData = false;
		return b;
	}
}
