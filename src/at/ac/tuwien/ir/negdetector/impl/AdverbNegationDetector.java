package at.ac.tuwien.ir.negdetector.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class AdverbNegationDetector 
extends BaseNegationDetector {
	protected static final String[] NEG_SIGS = {
		"not",
		"n't",
		"never",
		"nevermore"
	};
	
	private boolean isNegSignal(Tree leaf) {
		for (int i = 0; i < NEG_SIGS.length; i++) {
			if (leaf.label().value().equals(NEG_SIGS[i])) {
				return true;
			}
		}
		return false;
	}
	
	protected List<Tree> findNegationSignal(Tree root) {
		List<Tree> negSignals = new ArrayList<Tree>();
		List<Tree> leaves = root.getLeaves();
		for (Tree leaf: leaves) {
			if (isNegSignal(leaf)) {
				negSignals.add(leaf);
			}
		}
		return negSignals;
	}
	
	protected List<Tree> findNegationPattern(Tree negSignal) {
		return null;
	}
	
	protected List<Tree> findNegatedPhrase(Tree negSignal) {
		return null;
	}
	
	public List<Tree> detectNegation(Tree root) {
		List<Tree> negSignals = findNegationSignal(root);
		
		Map<Tree, List<Tree>> negPatterns = new HashMap<Tree, List<Tree>>();
		for (Tree negSignal: negSignals) {
			negPatterns.put(negSignal, findNegationPattern(negSignal));
		}
		
		Map<Tree, List<Tree>> negPhrases = new HashMap<Tree, List<Tree>>();
		for (Tree negSignal: negSignals) {
			negPhrases.put(negSignal, findNegatedPhrase(negSignal));
		}
		
		return null;
	}
	
}
