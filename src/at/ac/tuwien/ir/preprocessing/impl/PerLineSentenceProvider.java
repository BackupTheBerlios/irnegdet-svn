package at.ac.tuwien.ir.preprocessing.impl;

import java.io.File;
import java.io.IOException;

/**
 * Takes a file as input assuming that each sentence is in one line. 
 * @author Andreas Bernauer
 *
 */
public class PerLineSentenceProvider 
extends FileSentenceProvider {
	
	public PerLineSentenceProvider(File file) {
		super(file);
	}
	
	public void init() {
		super.init();
		try {
			setSentence(getIn().readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getNextSentence() {
		String sentence;
		if (!isInit()) {
			System.err.println(this.getClass().getSimpleName() + " not initialized.");
			return null;
		}
		if (!hasNextSentence()) {
			System.err.println("EOF has been reached.");
			return null;
		}
		sentence = getSentence();
		try {
			setSentence(getIn().readLine());
		} catch (IOException e) {
			setSentence(null);
			e.printStackTrace();
		}
		return sentence;
	}
}