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
		"])))",
		
		"(VP !> __) << SBAR=cuthere"
	};
	protected static final String NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE = 
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +	
			//"< /VBN|VBG/ | " +
			"< (/VBN|VBG/ !$++ ADJP) | " +
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
		"])",
		
		"(VP !> __) << SBAR=cuthere"
	};
		
	protected static final String NEG_PATTERN_TREGEX_BE_ADJECTIVE =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (ADJP < JJ) | " +
			"$++ (VP < (VBN $+ ADJP)) | " +
			"$++ (VP < (VB $+ (ADJP < JJ)))" +
		"])";
		
	protected static final String[] NEG_PATTERN_TREGEX_CUT_DO = {
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP < (VB [" +
			"$+ (S <: (VP < TO < (VP < (VB $++ __=cuthere)))) | " +
			"!$+ S !$++ VP $++ __=cuthere" +
		"]))))",
		
		"(VP !> __) << SBAR=cuthere"
	};
		
	protected static final String NEG_PATTERN_TREGEX_DO =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +
			"<: VB | " +
			"< (VB !$++ VP !$++ ADJP)" +
		"])))";
		/*
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +
			"<: VB | " +
			"< VB < (S <: (VP < TO < (VP < VB)))" +
		"])))";
		*/
	protected static final String NEG_PHRASE_TREGEX =
		"NP !<< NP [" +
			"!<< EX !>> VP !,, NP >> (S << (VP < RB)) | " +
			"!>> PP >> (VP < RB > S $-- (NP <: EX))" +
		"]";

	protected static final String NEG_PATTERN_SURGOP =
		"delete cuthere";
	
	protected List<Tree> findNegationSignal(Tree root) {
		List<Tree> negSignals = getMatches(root, NEG_SIG_TREGEX);
		return negSignals;
	}
	
	protected boolean hasMatches(Tree root, final String tregex) {
		TregexMatcher matcher = null;
		try {
			matcher = getMatcher(root, tregex);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return matcher.find();
	}
	
	protected List<Tree> getMatches(Tree root, final String tregex) {
		TregexMatcher matcher;
		List<Tree> negSignals = new ArrayList<Tree>();
		try {
			matcher = getMatcher(root, tregex);
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
	
	private List<Pair<TregexPattern, TsurgeonPattern>> fillCutPatterns(
				List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns, Tree root, 
				final String tregexPattern, final String[] tregexCutPattern, final String surgopPattern) 
	throws ParseException {
		if (hasMatches(root, tregexPattern)) {
			if (cutPatterns.size() < 1) {
				for (int i = 0; i < tregexCutPattern.length; i++) {
					if (hasMatches(root, tregexCutPattern[i])) {
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
	private List<Tree> fillNegationsPatterns(List<Tree> negPatterns, Tree root, final String tregex) {
		if (hasMatches(root, tregex)) {
			if (negPatterns.size() > 0) {
				System.err.println("Warning: More than one negation pattern was found. This is unusual and should not happen.");
			}
			negPatterns.addAll(getMatches(root, tregex));
		}
		return negPatterns;
	}
	protected List<Tree> findNegationPatterns(Tree negSignal, Tree root) {
		List<Tree> negPatterns = new ArrayList<Tree>();
		List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
		Tree vpRoot = negSignal.parent(root).deepCopy();
		try {
			cutPatterns = fillCutPatterns(cutPatterns, vpRoot,
					NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE,
					NEG_PATTERN_TREGEX_CUT_BE_VERB_PAST_PARTICIPLE,
					NEG_PATTERN_SURGOP);
			cutPatterns = fillCutPatterns(cutPatterns, vpRoot,
					NEG_PATTERN_TREGEX_BE_ADJECTIVE,
					NEG_PATTERN_TREGEX_CUT_BE_ADJECTIVE,
					NEG_PATTERN_SURGOP);
			cutPatterns = fillCutPatterns(cutPatterns, vpRoot,
					NEG_PATTERN_TREGEX_DO,
					NEG_PATTERN_TREGEX_CUT_DO,
					NEG_PATTERN_SURGOP);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (cutPatterns.size() > 0) {
			negPatterns.add(Tsurgeon.processPatternsOnTree(cutPatterns, vpRoot));
		} else {
			negPatterns = fillNegationsPatterns(negPatterns, vpRoot, NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE);
			negPatterns = fillNegationsPatterns(negPatterns, vpRoot, NEG_PATTERN_TREGEX_BE_ADJECTIVE);
			negPatterns = fillNegationsPatterns(negPatterns, vpRoot, NEG_PATTERN_TREGEX_DO);
			if (negPatterns.size() < 1) {
				System.err.println("Warning: No negation pattern found.");
			}
		}
if (negPatterns.size() > 0)
negPatterns.get(0).pennPrint();
		return negPatterns;
	}	
	
	protected List<Tree> findNegatedPhrase(Tree negSignal, Tree root) {
		List<Tree> negPhrases = new ArrayList<Tree>();
		Tree sRoot = negSignal.parent(root).parent(root).deepCopy();
		negPhrases = getMatches(sRoot, NEG_PHRASE_TREGEX);
		if (negPhrases.size() > 1) {
			System.err.println("Warning: More than one negated phrase was found. This is unusual and should not happen.");
		} else if (negPhrases.size() < 1) {
			System.err.println("Warning: No negation phrase found.");
		}
if (negPhrases.size() > 0)
negPhrases.get(0).pennPrint();
		return negPhrases;
	}
	
	public List<Tree> detectNegation(Tree root) {
		List<Tree> negSignals = findNegationSignal(root);
		Map<Tree, Tree> negPatterns = new HashMap<Tree, Tree>();
		List<Tree> retNegPatterns;
		List<Tree> retNegPhrases;
		for (Tree negSignal: negSignals) {
			retNegPatterns = findNegationPatterns(negSignal, root);
			if (retNegPatterns.size() > 0) {
				negPatterns.put(negSignal, retNegPatterns.get(0));
			}
		}
		Map<Tree, Tree> negPhrases = new HashMap<Tree, Tree>();
		for (Tree negSignal: negSignals) {
			retNegPhrases = findNegatedPhrase(negSignal, root);
			if (retNegPhrases.size() > 0) {
				negPhrases.put(negSignal, retNegPhrases.get(0));
			}
		}
		return null;
	}
}
