package at.ac.tuwien.ir.negdetector.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.ParseException;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;
import edu.stanford.nlp.util.Pair;

public class AdverbNegationDetector 
extends BaseNegationDetector {
	protected static final String NEG_SIG_TREGEX =
		"RB < not | < n't | < never";
	
	protected static final String NEG_PATTERN_TREGEX =
		"(VP !> __) [" + // VP must be root node
			// "Be" verb-past-participle
			"< (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +							// root has either VBP, VBZ, VBD or MD as subnode which has RB as immediate right neigbour which das VP as right neighbour which has either	
				"< (/VBN|VBG/ !$++ VP $++ __=cuthere) | " +						// VBN respectively VBG as subnode which hasn't VP as right neighbour but some node as right neighbour which is labelled as "delete" or
				"< (/VBN|VB/ $+ (VP < (/VB(N|G)/ !$++ VP $++ __=cuthere)))" +	// VBN respectively VB as subnode which has VP as immediate right neighbour which has VBN respectively VBG as subnode which hasn't VP as right neighbour bu some node as right neighbour which as labelled as "delete".						
			"]))) | " +
			
			// "Do"
			"< YYY | " +
			
			// "Be" adjective
			"< XXX" +
		"]";
	protected static final String NEG_PATTERN_SURGOP =
		"delete cuthere";
	/*
	setNegPatternTregex("(VP !> __) [" + // VP must be root node
			// "Be" verb-past-participle
							"< (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +							// root has either VBP, VBZ, VBD or MD as subnode which has RB as immediate right neigbour which das VP as right neighbour which has either	
								"< (/VBN|VBG/ !$++ VP $++ __=delete) | " +						// VBN respectively VBG as subnode which hasn't VP as right neighbour but some node as right neighbour which is labelled as "delete" or
								"< (/VBN|VB/ $+ (VP < (/VB(N|G)/ !$++ VP $++ __=delete)))" +	// VBN respectively VB as subnode which has VP as immediate right neighbour which has VBN respectively VBG as subnode which hasn't VP as right neighbour bu some node as right neighbour which as labelled as "delete".						
							"])))" +
		"]");
	*/
	protected static final String NEG_PHRASE_TREGEX =
		"";
	
	protected List<Tree> findNegationSignal(Tree root) {
		TregexMatcher matcher;
		List<Tree> negSignals = new ArrayList<Tree>();
		try {
			matcher = getMatcher(root, NEG_SIG_TREGEX);
			while(matcher.find()) {
				negSignals.add(matcher.getMatch());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return negSignals;
	}
	
	protected boolean matchNegationPattern(Tree negSignal, Tree root) {
		TregexMatcher matcher;
		Tree vpRoot = negSignal.parent(root);
		try {
			matcher = getMatcher(vpRoot, NEG_PATTERN_TREGEX);
			if (matcher.find()) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected Tree findNegationPattern(Tree negSignal, Tree root) {
		Tree negPattern = null;
		try {
			Pair<TregexPattern, TsurgeonPattern> pattern = new Pair<TregexPattern, TsurgeonPattern>(
					TregexPattern.compile(NEG_PATTERN_TREGEX), 
					Tsurgeon.parseOperation(NEG_PATTERN_SURGOP));
			List<Pair<TregexPattern, TsurgeonPattern>> patterns = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
			patterns.add(pattern);
			Tree vpRoot = negSignal.parent(root);
			
			negPattern = Tsurgeon.processPatternsOnTree(patterns, vpRoot);
		} catch (ParseException e) {
			e.printStackTrace();
		}
negPattern.pennPrint();
System.out.println();
		return negPattern;
	}
	
	protected Tree findNegatedPhrase(Tree negSignal, Tree root) {
		return null;
	}
	
	public List<Tree> detectNegation(Tree root) {
		List<Tree> negSignals = findNegationSignal(root);
		
		Map<Tree, Tree> negPatterns = new HashMap<Tree, Tree>();
		for (Tree negSignal: negSignals) {
matchNegationPattern(negSignal, root);
			negPatterns.put(negSignal, findNegationPattern(negSignal, root));
		}
		
		Map<Tree, Tree> negPhrases = new HashMap<Tree, Tree>();
		for (Tree negSignal: negSignals) {
			negPhrases.put(negSignal, findNegatedPhrase(negSignal, root));
		}
		
		return null;
	}
}
