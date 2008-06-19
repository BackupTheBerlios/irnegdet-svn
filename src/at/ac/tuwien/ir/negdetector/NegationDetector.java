package at.ac.tuwien.ir.negdetector;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public interface NegationDetector {

	public void init();
	public void init(String[] parserFlags);
	public void init(String parserFile);
	public void init(String parserFile, String[] parserFlags);
	public boolean isInit();
	public Tree parseSentence(String sentence);
	public List<Tree> detectNegation(Tree root);
}
