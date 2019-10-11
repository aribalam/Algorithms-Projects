package assignments.wordnet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {

    private final WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int maxSum = 0;
        String maxNounA = null;
        for (int i = 0; i < nouns.length; i++) {
            int sum = 0;
            for (int j = i+1; j < nouns.length; j++)
                sum += wordNet.distance(nouns[i], nouns[j]);
            if (sum > maxSum) {
                maxSum = sum;
                maxNounA = nouns[i];
            }
        }

        return maxNounA;
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
