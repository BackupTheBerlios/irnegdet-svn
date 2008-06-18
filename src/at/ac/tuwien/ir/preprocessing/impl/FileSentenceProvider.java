package at.ac.tuwien.ir.preprocessing.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class FileSentenceProvider 
extends BaseSentenceProvider {
	
	protected File file;
	protected BufferedReader in;
	protected String sentence;
	
	public FileSentenceProvider(File file) {
		setFile(file);
		setIn(null);
		setSentence(null);
	}
	
	public abstract String getNextSentence();
	
	public void init() {
		if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
			System.err.println("Cannot open file: " + file.getAbsolutePath());
		}
		try {
			setIn(new BufferedReader(new FileReader(getFile())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInit() {
		return (getIn() != null);
	}
	public void shutdown() {
		if (getIn() != null) {
			try {
				getIn().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		setIn(null);
		setSentence(null);
	}
	
	public boolean hasNextSentence() {
		return (getSentence() != null);
	}
	
	protected File getFile() {
		return file;
	}
	protected void setFile(File file) {
		this.file = file;
	}

	protected String getSentence() {
		return sentence;
	}

	protected void setSentence(String sentence) {
		this.sentence = sentence;
	}

	protected BufferedReader getIn() {
		return in;
	}

	protected void setIn(BufferedReader in) {
		this.in = in;
	}
}
