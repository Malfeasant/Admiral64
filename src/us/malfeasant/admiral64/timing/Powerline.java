package us.malfeasant.admiral64.timing;

public enum Powerline {
	NA(60), EU(50);
	final int cycles;
	private Powerline(int cycles) {
		this.cycles = cycles;
	}
}
