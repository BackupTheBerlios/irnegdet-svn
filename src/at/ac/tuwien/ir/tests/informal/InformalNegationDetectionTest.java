package at.ac.tuwien.ir.tests.informal;

import java.io.File;

import at.ac.tuwien.ir.negdetector.NegationDetector;
import at.ac.tuwien.ir.negdetector.impl.AdverbNegationDetector;
import at.ac.tuwien.ir.preprocessing.impl.PerLineSentenceProvider;
import edu.stanford.nlp.trees.Tree;

public class InformalNegationDetectionTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String sentence;
		Tree tree;
		NegationDetector detector = new AdverbNegationDetector();
		detector.init();
		PerLineSentenceProvider provider = new PerLineSentenceProvider(new File("./sentences.txt"));
		provider.init();
		
		while(provider.hasNextSentence()) {
			sentence = provider.getNextSentence();
			tree = detector.parseSentence(sentence);
			tree.pennPrint();
			//tree = tree.flatten();
			//tree.pennPrint();
			detector.detectNegation(tree);
		}
		
		provider.shutdown();
	}

}
