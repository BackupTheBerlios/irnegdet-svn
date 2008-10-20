package at.ac.tuwien.ir.out;

import at.ac.tuwien.ir.negdetector.NegationData;


public interface Outputter {

	public void init();
        public void setNegData(NegationData negData);
	public boolean isInit();
	public void write();
	public void shutdown();
}
