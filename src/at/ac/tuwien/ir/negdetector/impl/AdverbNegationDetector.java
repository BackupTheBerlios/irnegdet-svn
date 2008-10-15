package at.ac.tuwien.ir.negdetector.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import at.ac.tuwien.ir.negdetector.NegationData;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.ParseException;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;
import edu.stanford.nlp.trees.tregex.tsurgeon.Tsurgeon;
import edu.stanford.nlp.trees.tregex.tsurgeon.TsurgeonPattern;
import edu.stanford.nlp.util.Pair;

/**
 * Detects negations in sentences, by using a pos-tagger and applying regular-expressions 
 * to the tree that the pos-tagger returns.
 * @author Andreas Bernuaer
 *
 */
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
	
	protected List<Tree> findNegationSignals(Tree root) {
		List<Tree> negSignals = getMatches(root, NEG_SIG_TREGEX);
		getNegData().addNegationSignals(negSignals);
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
	
	private List<Tree> matchNodeNumbers(Tree negPhratternTree, Tree originalRoot) {
		List<Tree> matchedLeaves = new ArrayList<Tree>();
		Iterator<Tree> rootIterator = originalRoot.iterator();
		while (rootIterator.hasNext()) {
			Tree currentRootNode = rootIterator.next();
			if (currentRootNode.label().equals(negPhratternTree.getNodeNumber(1).label())) {
				if (matchChildren(currentRootNode, negPhratternTree.getNodeNumber(1), matchedLeaves)) {
					return matchedLeaves;
				} else {
					matchedLeaves.clear();
				}
			}
		}
		return null;
	}
	
	private boolean matchChildren(Tree currentRootNode, Tree currentNegPhratternNode, List<Tree> matchedLeaves) {
		if (currentRootNode.numChildren() < currentNegPhratternNode.numChildren()) {
			return false;
		}
		if (currentRootNode.isLeaf() && currentNegPhratternNode.isLeaf()) {
			matchedLeaves.add(currentRootNode);
			return true;
		}
		List<Tree> rootChildren = currentRootNode.getChildrenAsList();
		List<Tree> phratternChildren = currentNegPhratternNode.getChildrenAsList();
		boolean foundMatch;
		for (Tree phratternChild: phratternChildren) {
			foundMatch = false;
			for (Tree rootChild: rootChildren) {
				if (rootChild.label().equals(phratternChild.label())) {
					if (matchChildren(rootChild, phratternChild, matchedLeaves)) {
						foundMatch = true;
					}
				}
			}
			if (!foundMatch) {
				return false;
			}
		}
		return true;
	}

	private List<Pair<TregexPattern, TsurgeonPattern>> fillCutPatterns(
				List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns, Tree root, Tree negSignal,
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
				getNegData().setAppliedMoreThanOneTregexNegPattern(true, negSignal);
System.err.println("Warning: More than one pattern for the negation pattern applied.");
			}
		}
		return cutPatterns;
	}
	private List<List<Tree>> fillNegationsPatterns(List<List<Tree>> negPatterns, Tree root, Tree originalRoot, Tree negSignal, final String tregex) {
		if (hasMatches(root, tregex)) {
			if (negPatterns.size() > 0) {
				getNegData().setMoreThanOneFoundNegPattern(true, negSignal);
System.err.println("Warning: More than one pattern for the negation pattern found.");
			}
			for (Tree match: getMatches(root, tregex)) {
				negPatterns.add(matchNodeNumbers(match, originalRoot));
			}
		}
		return negPatterns;
	}
	protected List<List<Tree>> findNegationPatterns(Tree negSignal, Tree root) {
		List<List<Tree>> negPatterns = new ArrayList<List<Tree>>();
		List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
		Tree vpRoot = negSignal;
		while (!vpRoot.label().value().equals("VP")) {
			vpRoot = vpRoot.parent(root);
			if (vpRoot == null) {
				vpRoot = negSignal;
				break;
			}
		}
		Tree originalRoot = vpRoot;
		vpRoot = vpRoot.deepCopy();
		try {
			cutPatterns = fillCutPatterns(cutPatterns, vpRoot, negSignal,
					NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE,
					NEG_PATTERN_TREGEX_CUT_BE_VERB_PAST_PARTICIPLE,
					NEG_PATTERN_SURGOP);
			cutPatterns = fillCutPatterns(cutPatterns, vpRoot, negSignal,
					NEG_PATTERN_TREGEX_BE_ADJECTIVE,
					NEG_PATTERN_TREGEX_CUT_BE_ADJECTIVE,
					NEG_PATTERN_SURGOP);
			cutPatterns = fillCutPatterns(cutPatterns, vpRoot, negSignal,
					NEG_PATTERN_TREGEX_DO,
					NEG_PATTERN_TREGEX_CUT_DO,
					NEG_PATTERN_SURGOP);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (cutPatterns.size() > 0) {
			Tree negPatternTree = Tsurgeon.processPatternsOnTree(cutPatterns, vpRoot);
			negPatterns.add(matchNodeNumbers(negPatternTree, originalRoot));
		} else {
			negPatterns = fillNegationsPatterns(negPatterns, vpRoot, originalRoot, negSignal, NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE);
			negPatterns = fillNegationsPatterns(negPatterns, vpRoot, originalRoot, negSignal, NEG_PATTERN_TREGEX_BE_ADJECTIVE);
			negPatterns = fillNegationsPatterns(negPatterns, vpRoot, originalRoot, negSignal, NEG_PATTERN_TREGEX_DO);
			if (negPatterns.size() < 1) {
				getNegData().setNotFoundNegPattern(true, negSignal);
System.err.println("Warning: No negation pattern found.");
			}
		}
		getNegData().addNegationPatterns(negPatterns, negSignal);
		return negPatterns;
	}	
	
	protected List<List<Tree>> findNegatedPhrase(Tree negSignal, Tree root) {
		List<List<Tree>> negPhrases = new ArrayList<List<Tree>>();
		Tree sRoot = negSignal;
		while (!sRoot.label().value().equals("S")) {
			sRoot = sRoot.parent(root);
			if (sRoot == null) {
				sRoot = negSignal;
				break;
			}
		}
		Tree originalRoot = sRoot;
		sRoot = sRoot.deepCopy();
		for (Tree match: getMatches(sRoot, NEG_PHRASE_TREGEX)) {
			negPhrases.add(matchNodeNumbers(match, originalRoot));
		}
		if (negPhrases.size() > 1) {
			getNegData().setMoreThanOneFoundNegPhrase(true, negSignal);
System.err.println("Warning: More than one negated phrase was found.");
		} else if (negPhrases.size() < 1) {
			getNegData().setNotFoundNegPhrase(true, negSignal);
System.err.println("Warning: No negation phrase found.");
		}
		getNegData().addNegatedPhrases(negPhrases, negSignal);
		return negPhrases;
	}
	
	public NegationData detectNegation(Tree root) {
		setNegData(new NegationData(root));
		
		List<Tree> negSignals = findNegationSignals(root);
		
		for (Tree negSignal: negSignals) {
			findNegationPatterns(negSignal, root);
			findNegatedPhrase(negSignal, root);
		}
		getNegData().lock();
		return getNegData();
	}
}
