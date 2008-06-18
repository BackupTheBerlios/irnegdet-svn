package at.ac.tuwien.ir.out;

import java.io.PrintStream;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public interface Outputter {

	public void init(List<Tree> trees);
	public void write(PrintStream out);
}
