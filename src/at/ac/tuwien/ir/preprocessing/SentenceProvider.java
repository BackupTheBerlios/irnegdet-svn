package at.ac.tuwien.ir.preprocessing;

public interface SentenceProvider {

	public void init();
	public boolean isInit();
	public String getNextSentence();
	public boolean hasNextSentence();
	public void shutdown();
}
