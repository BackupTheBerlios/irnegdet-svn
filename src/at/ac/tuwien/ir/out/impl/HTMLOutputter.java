/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.ac.tuwien.ir.out.impl;

import at.ac.tuwien.ir.negdetector.NegationData;
import edu.stanford.nlp.trees.Tree;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dirk Wallerstorfer
 */
public class HTMLOutputter
        extends BaseOutputter {

    protected final String NO_WHITE_SPACE_ITEM = ".,;:";
    protected final String NEGATION_SIGNAL_COLOR = "red";
    protected final String NEGATION_PATTERN_COLOR = "blue";
    protected final String NEGATED_PHRASE_COLOR = "green";
    private String fileLocation;
    private NegationData negData;
    private PrintWriter out;
    private List<Tree> negationSignals;
    private List<List<Tree>> negationPatterns;
    private List<List<Tree>> negatedPhrases;
    private List<Tree> negationSignalLeaves;
    private List<Tree> negationPatternLeaves;
    private List<Tree> negatedPhraseLeaves;

    public HTMLOutputter() {
        setFileLocation("./NegationDetection.html");
        negData = null;
    }

    public HTMLOutputter(String fileLocation) {
        setFileLocation(fileLocation);
        negData = null;
    }

    public void init() {
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(fileLocation)));
            out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">\n");
            out.write("<html>\n<head><title>NegationDetection</title></head>\n<body bgcolor=\"lightgrey\" style=\"font:10pt Verdana\">\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        clearData();
    }

    public boolean isInit() {
        return ((getNegData() != null) && (out != null));
    }

    public void clearData() {
        negationSignals = new ArrayList<Tree>();
        negationPatterns = new ArrayList<List<Tree>>();
        negatedPhrases = new ArrayList<List<Tree>>();
        negationSignalLeaves = new ArrayList<Tree>();
        negationPatternLeaves = new ArrayList<Tree>();
        negatedPhraseLeaves = new ArrayList<Tree>();
    }

    private boolean isNegationSignal(int nodeNumber) {
        negationSignals = getNegData().getNegationSignals();
        for (Tree negationSignal : negationSignals) {
            negationSignalLeaves = negationSignal.getLeaves();
            for (Tree negationSignalLeaf : negationSignalLeaves) {
                if (negationSignalLeaf.nodeNumber(getNegData().getRoot()) == nodeNumber) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNegationPattern(int nodeNumber) {
        negationSignals = getNegData().getNegationSignals();
        for (Tree negationSignal : negationSignals) {
            negationPatterns = getNegData().getNegationPatterns(negationSignal);
            if (negationPatterns != null) {
	            for (List<Tree> listNegationPatterns : negationPatterns) {
	                for (Tree negationPattern : listNegationPatterns) {
	                    negationPatternLeaves = negationPattern.getLeaves();
	                    for (Tree negationPatternLeaf : negationPatternLeaves) {
	                        if (negationPatternLeaf.nodeNumber(getNegData().getRoot()) == nodeNumber) {
	                            return true;
	                        }
	                    }
	                }
	            }
            }
        }

        return false;
    }

    private boolean isNegatedPhrase(int nodeNumber) {
        negationSignals = getNegData().getNegationSignals();
        for (Tree negationSignal : negationSignals) {
            negatedPhrases = getNegData().getNegatedPhrases(negationSignal);
            if (negatedPhrases != null) {
	            for (List<Tree> listNegatedPhrase : negatedPhrases) {
	                for (Tree negatedPhrase : listNegatedPhrase) {
	                    negatedPhraseLeaves = negatedPhrase.getLeaves();
	                    for (Tree negatedPhraseLeaf : negatedPhraseLeaves) {
	                        if (negatedPhraseLeaf.nodeNumber(getNegData().getRoot()) == nodeNumber) {
	                            return true;
	                        }
	                    }
	                }
	            }
            }
        }

        return false;
    }

    protected void writeItem(String item, String color) {
        try {
            if (!this.NO_WHITE_SPACE_ITEM.contains(item)) {
                getOut().write(" ");
            }
            if (color == null) {
                getOut().write(item);
            } else {
                getOut().write("<span style=\"color:" + color + "\">" + item + "</span>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void writeRootSentence(Tree root) {
        List<Tree> rootLeaves;
        rootLeaves = root.getLeaves();

        try {
            getOut().write("<b>Sentence:</b>");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Tree rootLeaf : rootLeaves) {
            if (isNegationSignal(rootLeaf.nodeNumber(getNegData().getRoot()))) {
                writeItem(rootLeaf.value(), NEGATION_SIGNAL_COLOR);
            } else if (isNegationPattern(rootLeaf.nodeNumber(getNegData().getRoot()))) {
                writeItem(rootLeaf.value(), NEGATION_PATTERN_COLOR);
            } else if (isNegatedPhrase(rootLeaf.nodeNumber(getNegData().getRoot()))) {
                writeItem(rootLeaf.value(), NEGATED_PHRASE_COLOR);
            } else {
                writeItem(rootLeaf.value(), null);
            }
        }

        try {
            getOut().write("<br/>");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void writeNegationData() {

        negationSignals = getNegData().getNegationSignals();
        if (negationSignals.size() == 0) {
            writeItem("No negation Signals found in sentence.<br/><hr/>", null);
        } else {
            for (Tree negationSignal : negationSignals) {
                writeItem("\n<b>Negation Signal:</b> ", null);
                negationSignalLeaves = negationSignal.getLeaves();
                for (Tree negationSignalLeaf : negationSignalLeaves) {
                    writeItem(negationSignalLeaf.value(), NEGATION_SIGNAL_COLOR);
                }
                writeItem("<br/>", null);
                negationPatterns = getNegData().getNegationPatterns(negationSignal);
                
                if ((negationPatterns != null) && (negationPatterns.size() > 0)) {
                	writeItem("\n<b>Negation Pattern:</b> ", null);

	                for (List<Tree> listNegationPatterns : negationPatterns) {
	                    for (Tree negationPattern : listNegationPatterns) {
	                        negationPatternLeaves = negationPattern.getLeaves();
	                        for (Tree negationPatternLeaf : negationPatternLeaves) {
	                            writeItem(negationPatternLeaf.value(), NEGATION_PATTERN_COLOR);
	                        }
	                    }
	                }
	                writeItem("<br/>", null);
                } else {
                	writeItem("No negation Patterns found in sentence.<br/>", null);
                }

                negatedPhrases = getNegData().getNegatedPhrases(negationSignal);
                
                if ((negatedPhrases != null) && (negatedPhrases.size() > 0)) {
    	            writeItem("\n<b>Negated Phrase:</b> ", null);

                    for (List<Tree> listNegatedPhrase : negatedPhrases) {
	                    for (Tree negatedPhrase : listNegatedPhrase) {
	                        negatedPhraseLeaves = negatedPhrase.getLeaves();
	                        for (Tree negatedPhraseLeaf : negatedPhraseLeaves) {
	                            writeItem(negatedPhraseLeaf.value(), NEGATED_PHRASE_COLOR);
	                        }
	                    }
	                }
	                writeItem("<br/>", null);
                } else {
                	writeItem("No negated Phrases found in sentence.<br/>", null);
                }
            }
            writeItem("<hr/>", null);
        }
    }

    public void write() {
        writeRootSentence(getNegData().getRoot());
        writeNegationData();
        clearData();
    }

    public void shutdown() {
        try {
            out.write("</body>\n</html>\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
        setNegData(null);
        try {
            if (out != null) {
                out.flush();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public NegationData getNegData() {
        return negData;
    }

    public void setNegData(NegationData negData) {
        this.negData = negData;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }
}
