package at.ac.tuwien.ir.negdetector.impl;

import java.util.List;

import at.ac.tuwien.ir.negdetector.NegationDetector;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.tregex.ParseException;
import edu.stanford.nlp.trees.tregex.TregexMatcher;
import edu.stanford.nlp.trees.tregex.TregexPattern;

public abstract class BaseNegationDetector 
implements NegationDetector {
	protected static final String PARSER_FILE = "./lib/englishPCFG.ser.gz";
	protected static final String[] PARSER_FLAGS = {
		"-maxlength", "50",
		"-retainTmpSubcategories",
		"-outputFormat", "penn"
	};
	private LexicalizedParser parser;
	
	public abstract List<Tree> detectNegation(Tree root);
	protected abstract List<Tree> findNegationSignal(Tree root);
	protected abstract List<Tree> findNegationPattern(Tree negSignal);
	protected abstract List<Tree> findNegatedPhrase(Tree negSignal);
	
	public BaseNegationDetector() {
		setParser(null);
	}
	
	public void init() {
		setParser(new LexicalizedParser(PARSER_FILE));
		getParser().setOptionFlags(PARSER_FLAGS);
	}
	public void init(String[] parserFlags) {
		setParser(new LexicalizedParser(PARSER_FILE));
		getParser().setOptionFlags(parserFlags);
	}
	public void init(String parserFile) {
		setParser(new LexicalizedParser(parserFile));
		getParser().setOptionFlags(PARSER_FLAGS);
	}
	public void init(String parserFile, String[] parserFlags) {
		setParser(new LexicalizedParser(parserFile));
		getParser().setOptionFlags(parserFlags);
	}
	public boolean isInit() {
		return (getParser() != null);
	}
	
	public Tree parseSentence(String sentence) {
		return getParser().apply(sentence);
	}

	protected TregexMatcher getMatcher(Tree tree, String tregex) 
	throws ParseException {
		TregexPattern pattern = TregexPattern.compile(tregex);
		TregexMatcher matcher = pattern.matcher(tree);
		return matcher;
	}
	
	protected LexicalizedParser getParser() {
		return parser;
	}

	protected void setParser(LexicalizedParser parser) {
		this.parser = parser;
	}
}
