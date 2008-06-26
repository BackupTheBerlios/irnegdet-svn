package at.ac.tuwien.ir.tests.informal;

import java.io.File;

import at.ac.tuwien.ir.negdetector.NegationData;
import at.ac.tuwien.ir.negdetector.NegationDetector;
import at.ac.tuwien.ir.negdetector.impl.AdverbNegationDetector;
import at.ac.tuwien.ir.out.Outputter;
import at.ac.tuwien.ir.out.impl.CLIOutputter;
import at.ac.tuwien.ir.preprocessing.impl.PerLineSentenceProvider;
import edu.stanford.nlp.trees.Tree;

public class InformalNegationDetectionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sentence;
		Tree tree;
		Outputter outputter;
		NegationData negData;
		NegationDetector detector = new AdverbNegationDetector();
		detector.init();
		PerLineSentenceProvider provider = new PerLineSentenceProvider(new File("./sentences.txt"));
		provider.init();
		outputter = new CLIOutputter();
		while(provider.hasNextSentence()) {
			sentence = provider.getNextSentence();
			tree = detector.parseSentence(sentence);
			tree.pennPrint();
			negData = detector.detectNegation(tree);
			//outputter.init(negData);
			//outputter.write();
			//outputter.shutdown();
		}
		
		provider.shutdown();
	}

}
