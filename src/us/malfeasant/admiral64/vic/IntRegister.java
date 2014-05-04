package us.malfeasant.admiral64.vic;

class IntRegister extends Register {
	int contents;
	
	@Override
	int pack() {
		return contents;
	}

	@Override
	void unpack(int d) {
		contents = d;
	}

}
