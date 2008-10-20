package at.ac.tuwien.ir.out.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.ir.negdetector.NegationData;
import edu.stanford.nlp.trees.Tree;

public class CLIOutputter 
extends BaseOutputter {

	protected final String NO_WHITE_SPACE_ITEM = ".,;:";
	private PrintStream out;
	private NegationData negData;
	
	public CLIOutputter() {
		setOut(System.out);
	}
	public CLIOutputter(PrintStream ps) {
		setOut(ps);
	}
	public void init() {
		
	}
	public boolean isInit() {
		return (getNegData() != null);
	}
	public void shutdown() {
		setNegData(null);
	}
	protected void writeItem(String item) {
            if (!this.NO_WHITE_SPACE_ITEM.contains(item)) {
			getOut().print(" ");
		}
		getOut().print(item);
	}
	protected void writeRootSentence(Tree root) {
		List<Tree> rootLeaves;
		rootLeaves = root.getLeaves();
                writeItem("Sentence:");
		for (Tree rootLeaf: rootLeaves) {
			writeItem(rootLeaf.value());
		}
                writeItem("\n");
	}
	public void writeNegationData() {
		List<Tree> negationSignals = new ArrayList<Tree>();
		List<List<Tree>> negationPatterns = new ArrayList<List<Tree>>();
		List<List<Tree>> negatedPhrases = new ArrayList<List<Tree>>();
                
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
                        
                        for(List<Tree> listNegationPatterns: negationPatterns) {
                            for (Tree negationPattern: listNegationPatterns) {
                                    negationPatternLeaves = negationPattern.getLeaves();
                                    for (Tree negationPatternLeaf: negationPatternLeaves) {
                                            writeItem(negationPatternLeaf.value());
                                    }
                            }
                        }
                        
			negatedPhrases = negData.getNegatedPhrases(negationSignal);
			writeItem("\nNegated Phrase: ");
                        
                        for(List<Tree> listNegatedPhrase: negatedPhrases) {
                            for (Tree negatedPhrase: listNegatedPhrase) {
                                    negatedPhraseLeaves = negatedPhrase.getLeaves();
                                    for (Tree negatedPhraseLeaf: negatedPhraseLeaves) {
                                            writeItem(negatedPhraseLeaf.value());
                                    }
                            }
                        }
		}
	}

        public void write() {
		Tree root = getNegData().getRoot();
                writeItem("\n--------------------\n");
		writeRootSentence(root);
		writeNegationData();
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
	public void setNegData(NegationData negData) {
		this.negData = negData;
	}
}
