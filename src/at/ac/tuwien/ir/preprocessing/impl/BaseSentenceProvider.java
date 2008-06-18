package at.ac.tuwien.ir.preprocessing.impl;

import at.ac.tuwien.ir.preprocessing.SentenceProvider;

public abstract class BaseSentenceProvider 
implements SentenceProvider {
	
	public abstract String getNextSentence();
	public abstract boolean hasNextSentence();
	public abstract void shutdown();
	public abstract void init();
	public abstract boolean isInit();
	
}
