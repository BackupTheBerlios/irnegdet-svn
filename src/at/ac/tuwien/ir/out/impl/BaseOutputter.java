package at.ac.tuwien.ir.out.impl;

import java.io.PrintStream;
import java.util.List;

import at.ac.tuwien.ir.out.Outputter;
import edu.stanford.nlp.trees.Tree;

public abstract class BaseOutputter 
implements Outputter {

	public abstract void write(PrintStream out);
	
	public void init(List<Tree> trees) {
		
	}
}
