package at.ac.tuwien.ir.preprocessing.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FileSentenceProvider 
extends BaseSentenceProvider {
	
	private File file;
	private BufferedReader in;
	private String sentence;
	
	public FileSentenceProvider(File file) {
		setFile(file);
		setIn(null);
		setSentence(null);
	}
	
	public void init() {
		if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
			System.err.println("Cannot open file: " + file.getAbsolutePath());
		}
		try {
			setIn(new BufferedReader(new FileReader(getFile())));
			setSentence(getIn().readLine());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
	
	public String getNextSentence() {
		String sentence;
		if (getIn() == null) {
			System.err.println(this.getClass().getSimpleName() + " not initialized.");
			return null;
		}
		if (!hasNextSentence()) {
			System.err.println("EOF has been reached.");
			return null;
		}
		sentence = this.sentence;
		try {
			this.sentence = getIn().readLine();
		} catch (IOException e) {
			this.sentence = null;
			e.printStackTrace();
		}
		return sentence;
	}
	
	public boolean hasNextSentence() {
		return (getSentence() != null);
	}
	
	private File getFile() {
		return file;
	}
	private void setFile(File file) {
		this.file = file;
	}

	private String getSentence() {
		return sentence;
	}

	private void setSentence(String sentence) {
		this.sentence = sentence;
	}

	private BufferedReader getIn() {
		return in;
	}

	private void setIn(BufferedReader in) {
		this.in = in;
	}
}
