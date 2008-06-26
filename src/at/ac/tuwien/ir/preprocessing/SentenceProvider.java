package at.ac.tuwien.ir.preprocessing;

/**
 * 
 * Reads text from input-sources and classifies it as sentences. 
 * @author Andreas Bernauer
 *
 */
public interface SentenceProvider {

	public void init();
	public boolean isInit();
	public String getNextSentence();
	public boolean hasNextSentence();
	public void shutdown();
}
