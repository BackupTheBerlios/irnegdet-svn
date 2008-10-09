package at.ac.tuwien.ir.out.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.ac.tuwien.ir.negdetector.NegationData;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.trees.Tree;

public class CLIOutputter 
extends BaseOutputter {

	protected final String NO_WHITE_SPACE_ITEM = ".,;:";
	private PrintStream out;
	private NegationData negData;
//	private Map<Label, Integer> negationFlags;
//	private String formattingMode;
	
	public CLIOutputter() {
		setOut(System.out);
	}
	public CLIOutputter(PrintStream ps) {
		setOut(ps);
	}
	public void init(NegationData negData) {
		setNegData(negData);
//		setNegationFlags(new HashMap<Label, Integer>());
//		setFormattingMode("NegatedPhrase");
	}
/*	public void init(NegationData negData, String formattingMode) {
		setNegData(negData);
		setNegationFlags(new HashMap<Label, Integer>());
		setFormattingMode(formattingMode);
	}
*/	public boolean isInit() {
		return (getNegData() != null);
	}
	public void shutdown() {
		setNegData(null);
	}
	protected void writeItem(String item/*, boolean mark, Integer group*/) {
/*		if (!this.NO_WHITE_SPACE_ITEM.contains(item)) {
			getOut().print(" ");
		}
		if (!mark) {
			getOut().print(item);
		} else {
			for (int i = 0; i < item.length(); i++) {
				if (group != null && group.intValue() > 0) {
					if (i == 0) {
						getOut().print(group.intValue());
					} else {
						getOut().print("~");
					}
				} else {
					getOut().print(" ");
				}
			}
		}
*/		if (!this.NO_WHITE_SPACE_ITEM.contains(item)) {
			getOut().print(" ");
		}
		getOut().print(item);
	}
	protected void writeRootSentence(Tree root) {
		List<Tree> rootLeaves;
		rootLeaves = root.getLeaves();
		for (Tree rootLeaf: rootLeaves) {
			writeItem(rootLeaf.value()/*, false, 0*/);
		}
	}
	public void writeNegationData() {
		List<Tree> negationSignals = new ArrayList<Tree>();
		List<Tree> negationPatterns = new ArrayList<Tree>();
		List<Tree> negatedPhrases = new ArrayList<Tree>();
		List<Tree> negationSignalLeaves = new ArrayList<Tree>();
		List<Tree> negationPatternLeaves = new ArrayList<Tree>();
		List<Tree> negatedPhraseLeaves = new ArrayList<Tree>();
		NegationData negData = getNegData();
		negationSignals = negData.getNegationSignals();
		for (Tree negationSignal: negationSignals) {
			writeItem("\nNegation Signal: ");
			negationSignalLeaves = negationSignal.getLeaves();
			for (Tree negationSignalLeaf: negationSignalLeaves) {
				writeItem(negationSignalLeaf.value());
			}
			negationPatterns = negData.getNegationPatterns(negationSignal);
			writeItem("\nNegation Pattern: ");
			for (Tree negationPattern: negationPatterns) {
				negationPatternLeaves = negationPattern.getLeaves();
				for (Tree negationPatternLeaf: negationPatternLeaves) {
					writeItem(negationPatternLeaf.value());
				}
			}
			negatedPhrases = negData.getNegatedPhrases(negationSignal);
			writeItem("\nNegated Phrase: ");
			for (Tree negatedPhrase: negatedPhrases) {
				negatedPhraseLeaves = negatedPhrase.getLeaves();
				for (Tree negatedPhraseLeaf: negatedPhraseLeaves) {
					writeItem(negatedPhraseLeaf.value());
				}
			}
		}
	}
/*	protected void writeNegationFlags(Tree root) {
		setNegationFlags(new HashMap<Label, Integer>());
		List<Tree> rootLeaves = root.getLeaves();
		if (getFormattingMode().equalsIgnoreCase("NegationSignal")) {
			matchNegationSignalsFlags(root);
		} else if (getFormattingMode().equalsIgnoreCase("NegationPattern")) {
			matchNegationPatternsFlags(root);
		} else {
			matchNegationPatternsFlags(root);
			matchNegatedPhrasesFlags(root);
		}
		for (Tree rootLeaf: rootLeaves) {
			writeItem(rootLeaf.value(), true, getNegationFlags().get(rootLeaf.label()));
		}
	}
	protected void matchNegationSignalsFlags(Tree root) {
		int negationSignalNo;
		List<Tree> rootLeaves = root.getLeaves();
		List<Tree> negationSignals = getNegData().getNegationSignals();
		for (Tree rootLeaf: rootLeaves) {
			negationSignalNo = 1;
			for (Tree negationSignal: negationSignals) {
				for (Tree negationSignalLeaf: negationSignal.getLeaves()){
					if (rootLeaf.label().equals(negationSignalLeaf.label())) {
						getNegationFlags().put(negationSignalLeaf.label(), negationSignalNo);
					}
				}
				negationSignalNo++;
			}
		}
	}
	protected void matchNegationPatternsFlags(Tree root) {
		int negationSignalNo;
		List<Tree> rootLeaves = root.getLeaves();
		List<Tree> negationSignals = getNegData().getNegationSignals();
		List<List<Tree>> negationPatternsList = new ArrayList<List<Tree>>();
		for (Tree negationSignal: negationSignals) {
			negationPatternsList.add(getNegData().getNegationPatterns(negationSignal));
		}
		for (Tree rootLeaf: rootLeaves) {
			negationSignalNo = 1;
			for (Tree negationSignal: negationSignals) {
				for (List<Tree> negationPatterns: negationPatternsList) {
					for (Tree negationPattern: negationPatterns) {
						for (Tree negationPatternLeaf: negationPattern.getLeaves()){
							if (rootLeaf.label().equals(negationPatternLeaf.label())) {
								getNegationFlags().put(negationPatternLeaf.label(), negationSignalNo);
							}
						}
					}
				}
				negationSignalNo++;
			}
		}
	}
	protected void matchNegatedPhrasesFlags(Tree root) {
		int negationSignalNo;
		List<Tree> rootLeaves = root.getLeaves();
		List<Tree> negationSignals = getNegData().getNegationSignals();
		List<List<Tree>> negatedPhrasesList = new ArrayList<List<Tree>>();
		for (Tree negationSignal: negationSignals) {
			negatedPhrasesList.add(getNegData().getNegatedPhrases(negationSignal));
		}
		for (Tree rootLeaf: rootLeaves) {
			negationSignalNo = 1;
			for (Tree negationSignal: negationSignals) {
				for (List<Tree> negatedPhrases: negatedPhrasesList) {
					for (Tree negatedPhrase: negatedPhrases) {
						for (Tree negatedPhraseLeaf: negatedPhrase.getLeaves()){
							if (rootLeaf.label().equals(negatedPhraseLeaf.label())) {
								getNegationFlags().put(negatedPhraseLeaf.label(), negationSignalNo);
							}
						}
					}
				}
				negationSignalNo++;
			}
		}
	}
*/	public void write() {
		Tree root = getNegData().getRoot();
		writeRootSentence(root);
		writeItem("\n");
		writeNegationData();
//		writeNegationFlags(root);
		writeItem("\n");
	}
	private PrintStream getOut() {
		return out;
	}
	private void setOut(PrintStream out) {
		this.out = out;
	}
	private NegationData getNegData() {
		return negData;
	}
	private void setNegData(NegationData negData) {
		this.negData = negData;
	}
/*	private Map<Label, Integer> getNegationFlags() {
		return negationFlags;
	}
	private void setNegationFlags(Map<Label, Integer> negationFlags) {
		this.negationFlags = negationFlags;
	}
	private String getFormattingMode() {
		return formattingMode;
	}
	private void setFormattingMode(String formattingMode) {
		this.formattingMode = formattingMode;
	}
*/
}
