package at.ac.tuwien.ir.negdetector.impl;

import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ir.negdetector.NegationData;
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
		"RB >> S !$++ NP > VP [< not | < n't | < never]";
	
	protected static final String[] NEG_PATTERN_TREGEX_CUT_BE_VERB_PAST_PARTICIPLE = {
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (VP < (/VBN|VBG/ !$++ VP !$++ ADJP $++ __=cuthere)) | " +
			"$++ (VP < (/VBN|VB/ $+ (VP < (/VB(N|G)/ !$++ VP $++ __=cuthere))))" +
		"])",
		
		"(VP !> __) << SBAR=cuthere"
	};
	protected static final String NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE = 
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +	
			"$++ (VP < (/VBN|VBG/ !$++ ADJP)) | " +
			"$++ (VP < (/VBN|VB/ $+ (VP < (/VB(N|G)/))))" +
			//"$++ (ADJP)" +
		"])";
	
	protected static final String[] NEG_PATTERN_TREGEX_CUT_BE_ADJECTIVE = {
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (ADJP < (JJ $++ __=cuthere)) | " +
			"$++ (VP < (VBN $+ (ADJP $++ __=cuthere))) | " +
			"$++ (VP < (VB $+ (ADJP < (JJ $++ __=cuthere))))" +
		"])",
		
		"(VP !> __) << SBAR=cuthere",
		
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
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP < (VB [" +
			"$+ (S < (VP < TO < (VP < (VB $++ __=cuthere)))) | " +
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
			"!<< EX !>> VP [!,, NP | ,, S | ,, /,/] >+(NP) (S << (VP < RB)) | " +
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
	private boolean isInList(List<Tree> trees, Tree tree, Tree root) {
		for (Tree curTree: trees) {
			if (curTree.nodeNumber(root) == tree.nodeNumber(root)) {
				return true;
			}
		}
		return false;
	}
	protected List<Tree> getMatches(Tree root, final String tregex) {
		TregexMatcher matcher;
		List<Tree> negSignals = new ArrayList<Tree>();
		try {
			matcher = getMatcher(root, tregex);
			while(matcher.find()) {
				if (! isInList(negSignals, matcher.getMatch(), root)) {
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
				System.err.println("Warning: More than one pattern for the negation pattern applied.");
			}
		}
		return cutPatterns;
	}
	private List<Tree> fillNegationsPatterns(List<Tree> negPatterns, Tree root, final String tregex) {
		if (hasMatches(root, tregex)) {
			if (negPatterns.size() > 0) {
				System.err.println("Warning: More than one pattern for the negation pattern applied.");
			}
			negPatterns.addAll(getMatches(root, tregex));
		}
		return negPatterns;
	}
	protected List<Tree> findNegationPatterns(Tree negSignal, Tree root) {
		List<Tree> negPatterns = new ArrayList<Tree>();
		List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
		Tree vpRoot = negSignal;
		while (!vpRoot.label().value().equals("VP")) {
			vpRoot = vpRoot.parent(root);
			if (vpRoot == null) {
				vpRoot = negSignal;
				break;
			}
		}
		vpRoot = vpRoot.deepCopy();
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
		Tree sRoot = negSignal;
		while (!sRoot.label().value().equals("S")) {
			sRoot = sRoot.parent(root);
			if (sRoot == null) {
				sRoot = negSignal;
				break;
			}
		}
		sRoot = sRoot.deepCopy();
		negPhrases = getMatches(sRoot, NEG_PHRASE_TREGEX);
		if (negPhrases.size() > 1) {
			System.err.println("Warning: More than one negated phrase was found.");
		} else if (negPhrases.size() < 1) {
			System.err.println("Warning: No negation phrase found.");
		}
if (negPhrases.size() > 0)
negPhrases.get(0).pennPrint();
		return negPhrases;
	}
	
	public NegationData detectNegation(Tree root) {
		List<Tree> negPatterns;
		List<Tree> negPhrases;
		NegationData negData = new NegationData(root);
		
		List<Tree> negSignals = findNegationSignal(root);
		
		for (Tree negSignal: negSignals) {
			negPatterns = findNegationPatterns(negSignal, root);
			negPhrases = findNegatedPhrase(negSignal, root);
			negData.addNegationSignal(negSignal, negPatterns, negPhrases);
		}
		return negData;
	}
}
