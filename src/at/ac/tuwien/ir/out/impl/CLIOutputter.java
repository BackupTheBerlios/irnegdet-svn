package at.ac.tuwien.ir.out.impl;

import java.io.PrintStream;
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
	public void init(NegationData negData) {
		setNegData(negData);
	}
	public boolean isInit() {
		return (negData != null);
	}
	public void shutdown() {
		setNegData(null);
	}
	protected void writeItem(PrintStream out, String item) {
		//if (!)
	}
	protected void writeRootSentence(Tree root) {
		
	}
	
	public void write() {
		List<Tree> rootLeaves;
		rootLeaves = getNegData().getRoot().getLeaves();
		for (Tree rootLeaf: rootLeaves) {
			out.print(rootLeaf);
		}
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
}
