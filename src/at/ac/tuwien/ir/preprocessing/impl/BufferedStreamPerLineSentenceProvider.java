package at.ac.tuwien.ir.preprocessing.impl;

import java.io.BufferedInputStream;
import java.io.IOException;

public class BufferedStreamPerLineSentenceProvider 
extends BufferedStreamSentenceProvider {
	
	protected String sentence;

	public BufferedStreamPerLineSentenceProvider(BufferedInputStream inStream) {
		super(inStream);
		setSentence(null);
	}
	
	public BufferedStreamPerLineSentenceProvider() {
		super();
		setSentence(null);
	}
	
	public void init() {
		super.init();
		if (getIn() != null) {
			try {
				setSentence(getIn().readLine());
			} catch (IOException e) {
				e.printStackTrace();
			}
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