package us.malfeasant.admiral64.vic;

import java.util.function.Consumer;
import java.util.function.Predicate;

class SpriteWrangler {
	private final int COUNT = 8;
	private final SpriteControl[] spriteControl;
	
	SpriteWrangler() {
		spriteControl = new SpriteControl[COUNT];
		for (int i = 0; i < COUNT; ++i) {
			spriteControl[i] = new SpriteControl(i);
		}
	}
	
	int getLowX(int sprite) {
		return spriteControl[sprite].xPos & 0xff;
	}
	void setLowX(int sprite, int x) {
		spriteControl[sprite].xPos = (spriteControl[sprite].xPos & 0x100) | (x & 0xff);
	}
	
	int getY(int sprite) {
		return spriteControl[sprite].yPos;
	}
	void setY(int sprite, int y) {
		spriteControl[sprite].yPos = y;
	}
	
	int getHighX() {
		return gather(sc -> sc.xPos > 0xff);
	}
	void setHighX(int data) {
		scatter(sc -> sc.xPos = (sc.xPos & 0xff) | ((data & sc.weight) == 0 ? 0 : 0x100));
	}
	
	int getColor(int sprite) {
		return spriteControl[sprite].color.get();
	}
	void setColor(int sprite, int color) {
		spriteControl[sprite].color.set(color);
	}
	
	int getEnable() {
		return gather(sc -> sc.enable);
	}
	void setEnable(int data) {
		scatter(sc -> sc.enable = (data & sc.weight) != 0);
	}
	
	int getEnableMC() {
		return gather(sc -> sc.multicolor);
	}
	void setEnableMC(int data) {
		scatter(sc -> sc.multicolor = (data & sc.weight) != 0);
	}
	
	int getExpandX() {
		return gather(sc -> sc.xExpand);
	}
	void setExpandX(int data) {
		scatter(sc -> sc.xExpand = (data & sc.weight) != 0);
	}
	
	int getExpandY() {
		return gather(sc -> sc.yExpand);
	}
	void setExpandY(int data) {
		scatter(sc -> sc.yExpand = (data & sc.weight) != 0);
	}
	
	int getDataPri() {
		return gather(sc -> sc.dataPriority);
	}
	void setDataPri(int data) {
		scatter(sc -> sc.dataPriority = (data & sc.weight) != 0);
	}
	
	int getCollideS() {
		return gather(sc -> sc.getCollideS());
	}
	int getCollideD() {
		return gather(sc -> sc.getCollideD());
	}
	
	private void scatter(Consumer<SpriteControl> f) {
		for (SpriteControl sc : spriteControl) {
			f.accept(sc);
		}
	}
	private int gather(Predicate<SpriteControl> f) {
		int data = 0;
		for (SpriteControl sc : spriteControl) {
			if (f.test(sc)) {
				data |= sc.weight;
			}
		}
		return data;
	}
}
