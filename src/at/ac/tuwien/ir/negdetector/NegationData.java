package at.ac.tuwien.ir.negdetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

/**
 * This class is the output of the NegationDetector. It holds the whole input sentence,
 * all negation-signals found in the sentence and for every negation-signal:
 * - one or more negation pattern
 * - one or more negated pattern
 * - error flags, that indicate that either a negation pattern and/or negated phrase
 * was not found or that mor than one were found or that more than one regular expression 
 * applied. 
 * @author Andreas Bernauer
 *
 */
public class NegationData {

	private Tree root;
	private List<Tree> negationSignals;
	private Map<Integer, List<List<Tree>>> negationPatterns;
	private Map<Integer, List<List<Tree>>> negatedPhrases;
	private Map<Integer, NegationDataErrors> errorFlags;
	
	
	private boolean locked = false;
	
	/**
	 * Every NgationData must contain the sentence-root.
	 * @param root the sentence root
	 */
	public NegationData(Tree root) {
		setRoot(root);
		setNegationSignals(new ArrayList<Tree>());
		setNegationPatterns(new HashMap<Integer, List<List<Tree>>>());
		setNegatedPhrases(new HashMap<Integer, List<List<Tree>>>());
		setErrorFlags(new HashMap<Integer, NegationDataErrors>());
	}
	private boolean isLeafInRoot(Tree leaf) {
		List<Tree> rootLeaves = root.getLeaves();
		for (Tree rootLeaf: rootLeaves) {
			if (rootLeaf.nodeNumber(root) == leaf.nodeNumber(root)) {
				return true;
			}
		}
		return false;
	}
	private boolean areAllLeavesInRoot(Tree tree) {
		List<Tree> treeLeaves = tree.getLeaves();
		for (Tree treeLeaf: treeLeaves) {
			if (! isLeafInRoot(treeLeaf)) {
				return false;
			}
		}
		return true;
	}
	private boolean areAllLeavesInRoot(List tList) {
		if (tList.size() > 0) {
			if (tList.get(0) instanceof List) {
				for (List<Tree> treeLeaves: (List<List<Tree>>)tList) {
					for(Tree treeLeaf: treeLeaves){
						if (!isLeafInRoot(treeLeaf)) {
							return false;
						}
					}
				}
				return true;
			} else if (tList.get(0) instanceof Tree) {
				for (Tree tree: (List<Tree>)tList) {
					if (!areAllLeavesInRoot(tree)) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Adds a list of negation signals. The tree must be in the root tree.
	 * @param negSignals List of negation signals
	 * @return true if successfully added neation signals, false if there was 
	 * an error or the lock-flag is set. 
	 */
	public boolean addNegationSignals(List<Tree> negSignals) {
		if (isLocked()) {
			System.err.println("Locked.");
			return false;
		}
		if (root == null) {
			System.err.println("Root tree must be set.");
			return false;
		}
		for (Tree negSignal: negSignals) {
			if (negationSignals.contains(negSignal)) {
				return false;
			}
		}
		if (!areAllLeavesInRoot(negSignals)) {
			System.err.println("Negation Signal not in root tree.");
			return false;
		}
		getNegationSignals().addAll(negSignals);
		for (Tree negSignal: negSignals) {
			getErrorFlags().put(negSignal.nodeNumber(root), new NegationDataErrors());
		}
		return true;
	}
	
	/**
	 * Adds a list of negation patterns for one specific negation Signal. The negation 
	 * patterns must be in the root tree and the negation signal must be already added.
	 * @param negPatterns Negation patterns for the negation signal
	 * @param negSignal Negation signal
	 * @return true if successfully added the negation patterns, false if there was 
	 * an error or the lock-flag is set. 
	 */
	public boolean addNegationPatterns(List<List<Tree>> negPatterns, Tree negSignal) {
		if (isLocked()) {
			System.err.println("Locked.");
			return false;
		}
		if (root == null) {
			System.err.println("Root tree must be set.");
			return false;
		}
		if (! negationSignals.contains(negSignal)) {
			System.err.println("Appropriate Negation Signal must be added.");
		}
		if (!areAllLeavesInRoot(negPatterns)) {
			System.err.println("Negation Pattern not in root tree.");
			return false;
		}
		getNegationPatterns().put(negSignal.nodeNumber(root), negPatterns);
		return true;
	}
	
	/**
	 * Adds a list of negated phrases for one specific negation Signal. The negated 
	 * phrases must be in the root tree and the negation signal must be already added.
	 * @param negPhrases Negated phrases for the negation signal
	 * @param negSignal Negation signal
	 * @return true if successfully added the negated phrases, false if there was 
	 * an error or the lock-flag is set. 
	 */
	public boolean addNegatedPhrases(List<List<Tree>> negPhrases, Tree negSignal) {
		if (isLocked()) {
			System.err.println("Locked.");
			return false;
		}
		if (root == null) {
			System.err.println("Root tree must be set.");
			return false;
		}
		if (! negationSignals.contains(negSignal)) {
			System.err.println("Appropriate Negation Signal must be added.");
		}
		if (!areAllLeavesInRoot(negPhrases)) {
			System.err.println("Negated Phrase not in root tree.");
			return false;
		}
		getNegatedPhrases().put(negSignal.nodeNumber(root), negPhrases);
		return true;
	}
	
	/**
	 * Locks the object, the obejct is ro. 
	 */
	public void lock() {
		setLocked(true);
	}
	public Tree getRoot() {
		return root;
	}
	public List<Tree> getNegationSignals() {
		return negationSignals;
	}
	public List<List<Tree>> getNegationPatterns(Tree negationSignal) {
		return getNegationPatterns().get(negationSignal.nodeNumber(root));
	}
	public List<List<Tree>> getNegatedPhrases(Tree negationSignal) {
		return getNegatedPhrases().get(negationSignal.nodeNumber(root));
	}
	protected void setRoot(Tree root) {
		this.root = root;
	}
	protected void setNegationSignals(List<Tree> negationSignals) {
		this.negationSignals = negationSignals;
	}
	protected void setNegationPatterns(Map<Integer, List<List<Tree>>> negationPatterns) {
		this.negationPatterns = negationPatterns;
	}
	protected void setNegatedPhrases(Map<Integer, List<List<Tree>>> negPhrases) {
		this.negatedPhrases = negPhrases;
	}
	protected Map<Integer, List<List<Tree>>>getNegationPatterns() {
		return negationPatterns;
	}
	protected Map<Integer, List<List<Tree>>> getNegatedPhrases() {
		return negatedPhrases;
	}
	public boolean isNotFoundNegPattern(Tree negSignal) {
		if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
			return getErrorFlags().get(negSignal.nodeNumber(root)).isNotFoundNegPattern();
		}
		return false;
	}
	public boolean isNotFoundNegPhrase(Tree negSignal) {
		if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
			return getErrorFlags().get(negSignal.nodeNumber(root)).isNotFoundNegPhrase();
		}
		return false;
	}
	public boolean isAppliedMoreThanOneTregexNegPattern(Tree negSignal) {
		if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
			return getErrorFlags().get(negSignal.nodeNumber(root)).isAppliedMoreThanOneTregexNegPattern();
		}
		return false;
	}
	public boolean isAppliedMoreThanOneTregexNegPhrase(Tree negSignal) {
		if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
			return getErrorFlags().get(negSignal.nodeNumber(root)).isAppliedMoreThanOneTregexNegPhrase();
		}
		return false;
	}
	public boolean isMoreThanOneFoundNegPattern(Tree negSignal) {
		if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
			return getErrorFlags().get(negSignal.nodeNumber(root)).isMoreThanOneFoundNegPattern();
		}
		return false;
	}
	public boolean isMoreThanOneFoundNegPhrase(Tree negSignal) {
		if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
			return getErrorFlags().get(negSignal.nodeNumber(root)).isMoreThanOneFoundNegPhrase();
		}
		return false;
	}
	
	public void setNotFoundNegPattern(boolean notFoundNegPattern, Tree negSignal) {
		if (!isLocked()) {
			if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
				getErrorFlags().get(negSignal.nodeNumber(root)).setNotFoundNegPattern(notFoundNegPattern);
			}
		}
	}
	public void setNotFoundNegPhrase(boolean notFoundNegPhrase, Tree negSignal) {
		if (!isLocked()) {
			if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
				getErrorFlags().get(negSignal.nodeNumber(root)).setNotFoundNegPhrase(notFoundNegPhrase);
			}
		}
	}
	public void setAppliedMoreThanOneTregexNegPattern(boolean appliedMoreThanOneTregexNegPattern, Tree negSignal) {
		if (!isLocked()) {
			if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
				getErrorFlags().get(negSignal.nodeNumber(root)).setAppliedMoreThanOneTregexNegPattern(appliedMoreThanOneTregexNegPattern);
			}
		}
	}
	public void setAppliedMoreThanOneTregexNegPhrase(boolean appliedMoreThanOneTregexNegPhrase, Tree negSignal) {
		if (!isLocked()) {
			if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
				getErrorFlags().get(negSignal.nodeNumber(root)).setAppliedMoreThanOneTregexNegPhrase(appliedMoreThanOneTregexNegPhrase);
			}
		}
	}
	public void setMoreThanOneFoundNegPattern(boolean moreThanOneFoundNegPattern, Tree negSignal) {
		if (!isLocked()) {
			if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
				getErrorFlags().get(negSignal.nodeNumber(root)).setMoreThanOneFoundNegPattern(moreThanOneFoundNegPattern);
			}
		}
	}
	public void setMoreThanOneFoundNegPhrase(boolean moreThanOneFoundNegPhrase, Tree negSignal) {
		if (!isLocked()) {
			if (getErrorFlags().containsKey(negSignal.nodeNumber(root))) {
				getErrorFlags().get(negSignal.nodeNumber(root)).setMoreThanOneFoundNegPhrase(moreThanOneFoundNegPhrase);
			}
		}
	}
	public boolean isLocked() {
		return locked;
	}
	private void setLocked(boolean locked) {
		this.locked = locked;
	}
	private Map<Integer, NegationDataErrors> getErrorFlags() {
		return errorFlags;
	}
	private void setErrorFlags(Map<Integer, NegationDataErrors> errorFlags) {
		this.errorFlags = errorFlags;
	}
}
