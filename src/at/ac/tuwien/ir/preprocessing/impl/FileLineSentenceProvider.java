package at.ac.tuwien.ir.preprocessing.impl;

import java.io.File;
import java.io.IOException;

public class FileLineSentenceProvider 
extends FileSentenceProvider {
	
	protected String sentence;

	public FileLineSentenceProvider(File file) {
		super(file);
		setSentence(null);
	}
	
	public void init() {
		super.init();
		try {
			setSentence(getIn().readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean hasNextSentence() {
		return (getSentence() != null);
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
	
	public void shutdown() {
		super.shutdown();
		setSentence(null);
	}
	
	protected String getSentence() {
		return sentence;
	}

	protected void setSentence(String sentence) {
		this.sentence = sentence;
	}
}