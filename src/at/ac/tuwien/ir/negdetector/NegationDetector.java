package at.ac.tuwien.ir.negdetector;

import edu.stanford.nlp.trees.Tree;

/**
 * Detects negations in sentences.
 * @author Andreas Bernuaer
 *
 */
public interface NegationDetector {

	/**
	 * Initializes the NegationDetector with standard parser flags and standard parser file.
	 */
	public void init();
	
	/**
	 * Initializes the NegationDetector with parser flags and standard parser file.
	 */
	public void init(String[] parserFlags);
	
	/**
	 * Initializes the NegationDetector with standard parser flags and specified parser file.
	 */
	
	public void init(String parserFile);
	
	/**
	 * Initializes the NegationDetector with specified parser flags and specified parser file.
	 */
	public void init(String parserFile, String[] parserFlags);
	
	/**
	 * Checks if a NegationDetector was initialized.
	 * @return true if initialized, false if not initialized.
	 */
	public boolean isInit();
	
	/**
	 * Hands the sentence over to the parser.
	 * @param sentence One sentence
	 * @return The correspondig tree object of the input sentence.
	 */
	public Tree parseSentence(String sentence);
	
	/**
	 * Detects if there are some negation-patterns in one given sentence.
	 * @param root The corresponding tree object of one given sentence.
	 * @return An NegationData-object for the sentence-tree, that holds 
	 * all information according to possible negations in the sentence and 
	 * inconsistencies while detecting negations.
	 */
	public NegationData detectNegation(Tree root);
}
