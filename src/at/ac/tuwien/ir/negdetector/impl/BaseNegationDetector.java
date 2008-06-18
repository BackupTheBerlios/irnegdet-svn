package at.ac.tuwien.ir.negdetector.impl;

import java.util.List;

import at.ac.tuwien.ir.negdetector.NegationDetector;
import edu.stanford.nlp.trees.Tree;

public abstract class BaseNegationDetector 
implements NegationDetector {

	public abstract List<Tree> detectNegation(Tree tree);
}
