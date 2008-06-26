package at.ac.tuwien.ir.negdetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class NegationData {

	private Tree root;
	private List<Tree> negationSignals;
	private Map<Integer, List<Tree>> negationPatterns;
	private Map<Integer, List<Tree>> negatedPhrases;
	private Map<Integer, NegationDataErrors> errorFlags;
	
	
	private boolean locked = false;
	
	public NegationData(Tree root) {
		setRoot(root);
		setNegationSignals(new ArrayList<Tree>());
		setNegationPatterns(new HashMap<Integer, List<Tree>>());
		setNegatedPhrases(new HashMap<Integer, List<Tree>>());
		setErrorFlags(new HashMap<Integer, NegationDataErrors>());
	}
	private boolean isLeafInRoot(Tree leaf) {
		List<Tree> rootLeaves = root.getLeaves();
		for (Tree rootLeaf: rootLeaves) {
			if (rootLeaf.label().equals(leaf.label())) {
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
	private boolean areAllLeavesInRoot(List<Tree> tList) {
		for (Tree tree: tList) {
			if (!areAllLeavesInRoot(tree)) {
				return false;
			}
		}
		return true;
	}
	
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
	
	public boolean addNegationPatterns(List<Tree> negationPatterns, Tree negSignal) {
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
		if (!areAllLeavesInRoot(negationPatterns)) {
			System.err.println("Negation Pattern not in root tree.");
			return false;
		}
		getNegationPatterns().put(negSignal.nodeNumber(root), negationPatterns);
		return true;
	}
	
	public boolean addNegatedPhrases(List<Tree> negatedPhrases, Tree negSignal) {
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
		if (!areAllLeavesInRoot(negatedPhrases)) {
			System.err.println("Negated Phrase not in root tree.");
			return false;
		}
		getNegatedPhrases().put(negSignal.nodeNumber(root), negatedPhrases);
		return true;
	}

	public Tree getRoot() {
		return root;
	}
	public List<Tree> getNegationSignals() {
		return negationSignals;
	}
	public List<Tree> getNegationPatterns(Tree negationSignal) {
		return getNegationPatterns().get(negationSignal);
	}
	public List<Tree> getNegatedPhrases(Tree negationSignal) {
		return getNegatedPhrases().get(negationSignal);
	}
	protected void setRoot(Tree root) {
		this.root = root;
	}
	protected void setNegationSignals(List<Tree> negationSignals) {
		this.negationSignals = negationSignals;
	}
	protected void setNegationPatterns(Map<Integer, List<Tree>> negationPatterns) {
		this.negationPatterns = negationPatterns;
	}
	protected void setNegatedPhrases(Map<Integer, List<Tree>> negPhrases) {
		this.negatedPhrases = negPhrases;
	}
	protected Map<Integer, List<Tree>> getNegationPatterns() {
		return negationPatterns;
	}
	protected Map<Integer, List<Tree>> getNegatedPhrases() {
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
	public void lock() {
		setLocked(true);
	}
	private Map<Integer, NegationDataErrors> getErrorFlags() {
		return errorFlags;
	}
	private void setErrorFlags(Map<Integer, NegationDataErrors> errorFlags) {
		this.errorFlags = errorFlags;
	}
}
