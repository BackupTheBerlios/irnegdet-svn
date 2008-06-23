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
		"RB >> S < not | < n't | < never";
	
	protected static final String[] NEG_PATTERN_TREGEX_CUT_BE_VERB_PAST_PARTICIPLE = {
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +
			"< (/VBN|VBG/ !$++ VP !$++ ADJP $++ __=cuthere) | " +
			"< (/VBN|VB/ $+ (VP < (/VB(N|G)/ !$++ VP $++ __=cuthere)))" +
		"])))"
	};
	protected static final String NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE = 
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +	
			"< (/VBN|VBG/) | " +
			"< (/VBN|VB/ $+ (VP < (/VB(N|G)/)))" +
		"])))";
	
	protected static final String[] NEG_PATTERN_TREGEX_CUT_BE_ADJECTIVE = {
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (ADJP < (JJ $++ __=cuthere)) | " +
			"$++ (VP < (VBN $+ (ADJP $++ __=cuthere))) | " +
			"$++ (VP < (VB $+ (ADJP < (JJ $++ __=cuthere))))" +
		"])",
		
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (VP < (VBN $+ (ADJP < (JJ $++ __=cuthere))))" +
		"])"
	};
		
	protected static final String NEG_PATTERN_TREGEX_BE_ADJECTIVE =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (ADJP < JJ) | " +
			"$++ (VP < (VBN $+ ADJP)) | " +
			"$++ (VP < (VB $+ (ADJP < JJ)))" +
		"])";
		
	protected static final String[] NEG_PATTERN_TREGEX_CUT_DO = {
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP " +
			"< VB < (S <: (VP < TO < (VP < (VB $++ __=cuthere))))" +
		")))"
	};
		
	protected static final String NEG_PATTERN_TREGEX_DO =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +
			"<: VB | " +
			"< VB < (S <: (VP < TO < (VP < VB)))" +
		"])))";
	
	protected static final String NEG_PHRASE_TREGEX =
		"NP !<< NP [" +
			"!<< EX ! >> VP >> (S << (VP < RB)) | " +
			">> (VP < RB > S $-- (NP <: EX))" +
		"]";

	protected static final String NEG_PATTERN_SURGOP =
		"delete cuthere";
	
	protected List<Tree> findNegationSignal(Tree root) {
		TregexMatcher matcher;
		List<Tree> negSignals = new ArrayList<Tree>();
		try {
			matcher = getMatcher(root, NEG_SIG_TREGEX);
			while(matcher.find()) {
				if (! negSignals.contains(matcher.getMatch())) {
					negSignals.add(matcher.getMatch());
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return negSignals;
	}
	
	protected Tree getFirstPatternMatch(Tree negSignal, Tree root, final String pattern) {
		TregexMatcher matcher;
		try {
			matcher = getMatcher(root, pattern);
			if (matcher.find()) {
				return matcher.getMatch();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<Pair<TregexPattern, TsurgeonPattern>> fillCutPatterns(
				List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns, Tree negSignal, Tree root, 
				final String tregexPattern, final String[] tregexCutPattern, final String surgopPattern) 
	throws ParseException {
		if (getFirstPatternMatch(negSignal, root, tregexPattern) != null) {
			if (cutPatterns.size() < 1) {
				for (int i = 0; i < tregexCutPattern.length; i++) {
					if (getFirstPatternMatch(negSignal, root, tregexCutPattern[i]) != null) {
						Pair<TregexPattern, TsurgeonPattern> patternBeVerbPastParticiple = new Pair<TregexPattern, TsurgeonPattern>(
								TregexPattern.compile(tregexCutPattern[i]), 
								Tsurgeon.parseOperation(surgopPattern));
						cutPatterns.add(patternBeVerbPastParticiple);
					}
				}
			} else {
				System.err.println("Warning: More than one negation pattern was found. This is unusual and should not happen.");
			}
		}
		return cutPatterns;
	}
	protected Tree findNegationPattern(Tree negSignal, Tree root) {
		Tree negPattern = null;
		List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
		Tree vpRoot = negSignal.parent(root).deepCopy();
		try {
			cutPatterns = fillCutPatterns(cutPatterns, negSignal, vpRoot,
					NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE,
					NEG_PATTERN_TREGEX_CUT_BE_VERB_PAST_PARTICIPLE,
					NEG_PATTERN_SURGOP);
			cutPatterns = fillCutPatterns(cutPatterns, negSignal, vpRoot,
					NEG_PATTERN_TREGEX_BE_ADJECTIVE,
					NEG_PATTERN_TREGEX_CUT_BE_ADJECTIVE,
					NEG_PATTERN_SURGOP);
			cutPatterns = fillCutPatterns(cutPatterns, negSignal, vpRoot,
					NEG_PATTERN_TREGEX_DO,
					NEG_PATTERN_TREGEX_CUT_DO,
					NEG_PATTERN_SURGOP);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (cutPatterns.size() > 0) {
			negPattern = Tsurgeon.processPatternsOnTree(cutPatterns, vpRoot);
		} else {
			if (((negPattern = getFirstPatternMatch(negSignal, vpRoot, NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE)) == null) &&
				((negPattern = getFirstPatternMatch(negSignal, vpRoot, NEG_PATTERN_TREGEX_BE_ADJECTIVE)) == null) &&
				((negPattern = getFirstPatternMatch(negSignal, vpRoot, NEG_PATTERN_TREGEX_DO)) == null)) {
				System.err.println("Warning: No negation pattern found.");
			}
		}
if (negPattern != null)
negPattern.pennPrint();
		return negPattern;
	}	
	
	protected Tree findNegatedPhrase(Tree negSignal, Tree root) {
		Tree negPhrase = null;
		Tree sRoot = negSignal.parent(root).parent(root).deepCopy();
		if ((negPhrase = getFirstPatternMatch(negSignal, sRoot, NEG_PHRASE_TREGEX)) == null) {
			System.err.println("Warning: No negation phrase found.");
		}
if (negPhrase != null)
negPhrase.pennPrint();
		return negPhrase;
	}
	
	public List<Tree> detectNegation(Tree root) {
		List<Tree> negSignals = findNegationSignal(root);
		Map<Tree, Tree> negPatterns = new HashMap<Tree, Tree>();
		for (Tree negSignal: negSignals) {
			negPatterns.put(negSignal, findNegationPattern(negSignal, root));
		}
		Map<Tree, Tree> negPhrases = new HashMap<Tree, Tree>();
		for (Tree negSignal: negSignals) {
			negPhrases.put(negSignal, findNegatedPhrase(negSignal, root));
		}
		return null;
	}
}
