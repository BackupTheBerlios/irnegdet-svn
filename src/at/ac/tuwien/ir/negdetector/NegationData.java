package at.ac.tuwien.ir.negdetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class NegationData {

	private Tree root = null;
	private List<Tree> negationSignals;
	private Map<Tree, List<Tree>> negationPatterns;
	private Map<Tree, List<Tree>> negatedPhrases;
	
	public NegationData(Tree root) {
		setRoot(root);
		setNegationSignals(new ArrayList<Tree>());
		setNegationPatterns(new HashMap<Tree, List<Tree>>());
		setNegatedPhrases(new HashMap<Tree, List<Tree>>());
	}
	public boolean addNegationSignal(Tree negSignal, List<Tree> negationPatterns, List<Tree> negatedPhrases) {
		if (root == null) {
			System.err.println("Root tree must be set.");
			return false;
		}
		if (negationSignals.contains(negSignal)) {
			return false;
		}
		getNegationSignals().add(negSignal);
		getNegationPatterns().put(negSignal, negationPatterns);
		getNegatedPhrases().put(negSignal, negatedPhrases);
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
	protected void setNegationSignals(List<Tree> negationSignals) {
		this.negationSignals = negationSignals;
	}
	protected void setNegationPatterns(Map<Tree, List<Tree>> negationPatterns) {
		this.negationPatterns = negationPatterns;
	}
	protected void setNegatedPhrases(Map<Tree, List<Tree>> negPhrases) {
		this.negatedPhrases = negPhrases;
	}
	protected Map<Tree, List<Tree>> getNegationPatterns() {
		return negationPatterns;
	}
	protected Map<Tree, List<Tree>> getNegatedPhrases() {
		return negatedPhrases;
	}
	protected void setRoot(Tree root) {
		this.root = root;
	}
	
}
