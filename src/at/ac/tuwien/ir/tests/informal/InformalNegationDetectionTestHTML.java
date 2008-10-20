/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package at.ac.tuwien.ir.tests.informal;

import at.ac.tuwien.ir.negdetector.NegationData;
import at.ac.tuwien.ir.negdetector.NegationDetector;
import at.ac.tuwien.ir.negdetector.impl.AdverbNegationDetector;
import at.ac.tuwien.ir.out.Outputter;
import at.ac.tuwien.ir.out.impl.HTMLOutputter;
import at.ac.tuwien.ir.preprocessing.impl.PerLineSentenceProvider;
import edu.stanford.nlp.trees.Tree;
import java.io.File;

/**
 *
 * @author Dirk Wallerstorfer
 */
public class InformalNegationDetectionTestHTML {

    public static void main(String args[]) {
        String sentence;
        Tree tree;
        Outputter outputter;
        NegationData negData;
        NegationDetector detector = new AdverbNegationDetector();
        detector.init();
        PerLineSentenceProvider provider = new PerLineSentenceProvider(new File("./sentences.txt"));
        provider.init();
        outputter = new HTMLOutputter();
        outputter.init();
        while (provider.hasNextSentence()) {
            sentence = provider.getNextSentence();
            tree = detector.parseSentence(sentence);
            negData = detector.detectNegation(tree);
            outputter.setNegData(negData);
            outputter.write();
        }
        outputter.shutdown();

        provider.shutdown();
    }
    
}
