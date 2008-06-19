package at.ac.tuwien.ir.preprocessing.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public abstract class FileSentenceProvider 
extends BaseSentenceProvider {
	
	private File file;
	private BufferedReader in;
	private String sentence;
	
	public FileSentenceProvider(File file) {
		setFile(file);
		setIn(null);
		setSentence(null);
	}
	
	public abstract String getNextSentence();
	
	public void init() {
		if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
			System.err.println("Cannot open file: " + file.getAbsolutePath());
			System.exit(1);
		}
		try {
			setIn(new BufferedReader(new FileReader(getFile())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
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
		if (!isInit()) {
			System.err.println(this.getClass().getSimpleName() + " not initialized.");
			return false;
		}
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
