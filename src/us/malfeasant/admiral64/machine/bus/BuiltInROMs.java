package us.malfeasant.admiral64.machine.bus;

import java.io.IOException;
import java.io.InputStream;

public enum BuiltInROMs {
	KERNAL1("kernal.901227-01.bin"),
	KERNAL2("kernal.901227-02.bin"),
	KERNAL3("kernal.901227-03.bin"),
	BASIC("basic.901226-01.bin"),
	CHAR("characters.901225-01.bin", 12);
	
	private final String filename;
	private final int abits;
	
	private BuiltInROMs(String fn, int b) {
		filename = "ROMs/" + fn;
		abits = b;
	}
	private BuiltInROMs(String fn) {
		this(fn, 13);
	}
	
	public ROM load() throws IOException {
		InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
		assert is != null : "ROM \"" + filename + "\" failed to load.";
		byte[] bytes = new byte[2 ^ abits];
		is.read(bytes);
		return new ROM(abits, bytes);
	}
}
