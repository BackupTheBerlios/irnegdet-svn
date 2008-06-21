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
	
	protected static final String NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE_CUT =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +
			"< (/VBN|VBG/ !$++ VP !$++ ADJP $++ __=cuthere) | " +
			"< (/VBN|VB/ $+ (VP < (/VB(N|G)/ !$++ VP $++ __=cuthere)))" +
		"])))";
	protected static final String NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE_NOCUT =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +	
			"< (/VBN|VBG/ !$++ __) | " +
			"< (/VBN|VB/ $+ (VP < (/VB(N|G)/ !$++ __)))" +
		"])))";
	
	protected static final String NEG_PATTERN_TREGEX_BE_ADJECTIVE_CUT =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (ADJP < (JJ $++ __=cuthere)) | " +
			"$++ (VP < (VBN $+ (ADJP $++ __=cuthere))) | " +
			"$++ (VP < (VB $+ (ADJP < (JJ $++ __=cuthere))))" +
		"])";
		
	protected static final String NEG_PATTERN_TREGEX_BE_ADJECTIVE_NOCUT =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ [" +
			"$++ (ADJP < (JJ !$++ __)) | " +
			"$++ (VP < (VBN $+ (ADJP !$++ __))) | " +
			"$++ (VP < (VB $+ (ADJP < (JJ !$++ __))))" +
		"])";
		
	protected static final String NEG_PATTERN_TREGEX_DO_CUT =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP " +
			"< VB < (S <: (VP < TO < (VP < (VB $++ __=cuthere))))" +
		")))";
		
	protected static final String NEG_PATTERN_TREGEX_DO_NOCUT =
		"(VP !> __) < (/VBP|VBZ|VBD|MD/ $+ (RB $++ (VP [" +
			"<: VB | " +
			"< VB < (S <: (VP < TO < (VP < (VB !$++ __))))" +
		"])))";
		
	protected static final String NEG_PATTERN_SURGOP =
		"delete cuthere";
	
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
	
	protected Tree matchNegationPattern(Tree negSignal, Tree root, final String pattern) {
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
	
	protected Tree findNegationPattern(Tree negSignal, Tree root) {
		Tree negPattern = null;
		List<Pair<TregexPattern, TsurgeonPattern>> cutPatterns = new ArrayList<Pair<TregexPattern, TsurgeonPattern>>();
		Tree vpRoot = negSignal.parent(root);
		
		try {
			if (matchNegationPattern(negSignal, vpRoot, NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE_CUT) != null) {
				Pair<TregexPattern, TsurgeonPattern> patternBeVerbPastParticiple = new Pair<TregexPattern, TsurgeonPattern>(
						TregexPattern.compile(NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE_CUT), 
						Tsurgeon.parseOperation(NEG_PATTERN_SURGOP));
				cutPatterns.add(patternBeVerbPastParticiple);
			}
			if (matchNegationPattern(negSignal, vpRoot, NEG_PATTERN_TREGEX_BE_ADJECTIVE_CUT) != null) {
				Pair<TregexPattern, TsurgeonPattern> patternBeAdjective = new Pair<TregexPattern, TsurgeonPattern>(
						TregexPattern.compile(NEG_PATTERN_TREGEX_BE_ADJECTIVE_CUT), 
						Tsurgeon.parseOperation(NEG_PATTERN_SURGOP));
				cutPatterns.add(patternBeAdjective);
			}
			if (matchNegationPattern(negSignal, vpRoot, NEG_PATTERN_TREGEX_DO_CUT) != null) {
				Pair<TregexPattern, TsurgeonPattern> patternDo = new Pair<TregexPattern, TsurgeonPattern>(
						TregexPattern.compile(NEG_PATTERN_TREGEX_DO_CUT), 
						Tsurgeon.parseOperation(NEG_PATTERN_SURGOP));
				cutPatterns.add(patternDo);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (cutPatterns.size() > 1) {
			System.err.println("Warning: More than one negation pattern applied to the tree. This is unusual; ignoring negation.");
		} else if (cutPatterns.size() == 1) {
			negPattern = Tsurgeon.processPatternsOnTree(cutPatterns, vpRoot);
			
		} else if (	((negPattern = matchNegationPattern(negSignal, vpRoot, NEG_PATTERN_TREGEX_BE_VERB_PAST_PARTICIPLE_NOCUT)) == null) &&
					((negPattern = matchNegationPattern(negSignal, vpRoot, NEG_PATTERN_TREGEX_BE_ADJECTIVE_NOCUT)) == null) &&
					((negPattern = matchNegationPattern(negSignal, vpRoot, NEG_PATTERN_TREGEX_DO_NOCUT)) == null)
				  ) {
			System.err.println("Warning: Not match found for negation.");
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
			negPatterns.put(negSignal, findNegationPattern(negSignal, root));
		}
		
		Map<Tree, Tree> negPhrases = new HashMap<Tree, Tree>();
		for (Tree negSignal: negSignals) {
			negPhrases.put(negSignal, findNegatedPhrase(negSignal, root));
		}
		
		return null;
	}
}
