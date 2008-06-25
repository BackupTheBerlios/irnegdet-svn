package at.ac.tuwien.ir.out.impl;

import at.ac.tuwien.ir.negdetector.NegationData;
import at.ac.tuwien.ir.out.Outputter;

public abstract class BaseOutputter 
implements Outputter {

	public abstract void init(NegationData negData);
	public abstract boolean isInit();
	public abstract void write();
	public abstract void shutdown();
}
