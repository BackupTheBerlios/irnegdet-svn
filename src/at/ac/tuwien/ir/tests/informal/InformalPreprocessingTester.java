package at.ac.tuwien.ir.tests.informal;

import java.io.File;

import at.ac.tuwien.ir.preprocessing.impl.PerLineSentenceProvider;

public class InformalPreprocessingTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String line;
		PerLineSentenceProvider provider = new PerLineSentenceProvider(new File("./brown_neg.txt"));
		provider.init();
		while(provider.hasNextSentence()) {
			line = provider.getNextSentence();
			System.out.println(line);
		}
		provider.shutdown();
	}

}
