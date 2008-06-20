package at.ac.tuwien.ir.negdetector.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.ParseException;
import edu.stanford.nlp.trees.tregex.TregexMatcher;

public class AdverbNegationDetector 
extends BaseNegationDetector {
	protected static final String[] NEG_SIGS = {
		"not",
		"n't",
		"never"//,
		//"nevermore"
	};
	
	protected List<Tree> findNegationSignal(Tree root) {
		String tregex = "RB ";
		TregexMatcher matcher;
		List<Tree> negSignals = new ArrayList<Tree>();
		if (NEG_SIGS.length > 0) {
			tregex += "< " + NEG_SIGS[0];
			for (int i = 1; i < NEG_SIGS.length; i++) {
				tregex += " | < " + NEG_SIGS[i];
			}
			try {
				matcher = getMatcher(root, tregex);
				while(matcher.find()) {
					negSignals.add(matcher.getMatch());
				}
			} catch (ParseException e) {
				e.printStackTrace();
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
