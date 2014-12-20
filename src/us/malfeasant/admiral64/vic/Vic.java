package us.malfeasant.admiral64.vic;

import us.malfeasant.admiral64.plumbing.AddressProvider;
import us.malfeasant.admiral64.plumbing.DataProvider;
import us.malfeasant.admiral64.plumbing.Interrupter;

public class Vic implements DataProvider, AddressProvider, Interrupter {
	Registers regs = new Registers();
	
	@Override
	public int getData() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAddress() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean checkInterrupt() {
		return regs.checkInterrupt();
	}
}
