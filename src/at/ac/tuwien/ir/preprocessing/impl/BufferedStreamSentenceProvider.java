package at.ac.tuwien.ir.preprocessing.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class BufferedStreamSentenceProvider 
extends BaseSentenceProvider {
	
	protected BufferedReader in;
	protected InputStream inStream;
	
	public BufferedStreamSentenceProvider(BufferedInputStream inStream) {
		setIn(null);
		setInStream(inStream);
	}
	
	public BufferedStreamSentenceProvider() {
		setIn(null);
		setInStream(null);
	}
	
	public abstract String getNextSentence();
	public abstract boolean hasNextSentence();
	
	public void init() {
		/*if ((!file.exists()) || (!file.isFile()) || (!file.canRead())) {
			System.err.println("Cannot open file: " + file.getAbsolutePath());
		}
		try {
			setIn(new BufferedReader(new FileReader(getFile())));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}*/
		if (getInStream() != null) {
			setIn(new BufferedReader(new InputStreamReader(getInStream())));
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
	}

	protected BufferedReader getIn() {
		return in;
	}

	protected void setIn(BufferedReader in) {
		this.in = in;
	}

	protected InputStream getInStream() {
		return inStream;
	}

	protected void setInStream(InputStream inStream) {
		this.inStream = inStream;
	}
}
