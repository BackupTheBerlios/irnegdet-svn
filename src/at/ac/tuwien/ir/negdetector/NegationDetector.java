package at.ac.tuwien.ir.negdetector;

import java.util.List;

import edu.stanford.nlp.trees.Tree;

public interface NegationDetector {

	public List<Tree> detectNegation(Tree tree);
}
