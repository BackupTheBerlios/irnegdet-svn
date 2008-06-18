package at.ac.tuwien.ir.preprocessing.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class FilePerLineSentenceProvider 
extends BufferedStreamPerLineSentenceProvider {
	
	protected File file;

	public FilePerLineSentenceProvider(File file) {
		super();
		setFile(file);
	}
	
	public void init() {
		if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
			System.err.println("Cannot open file: " + file.getAbsolutePath());
		}
		try {
			setInStream(new FileInputStream(getFile()));
			setIn(new BufferedReader(new InputStreamReader(getInStream())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
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

	protected File getFile() {
		return file;
	}

	protected void setFile(File file) {
		this.file = file;
	}
	
}