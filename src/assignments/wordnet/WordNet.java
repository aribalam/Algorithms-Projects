package assignments.wordnet;

import edu.princeton.cs.algs4.BST;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.LinkedList;

public class WordNet {

    private final Digraph digraph;
    private final BST<String, LinkedList<Integer>> nouns;
    private final HashMap<Integer, String> synsetsData;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        nouns = new BST<>();
        this.synsetsData = new HashMap<>();

        In in = new In(synsets);

        String line;
        int count = 0;
        while (in.hasNextLine()) {
            line = in.readLine();
            String[] values = line.split(",");
            int vertex = Integer.parseInt(values[0]);
            this.synsetsData.put(vertex, values[1]);
            String[] nounList = values[1].split(" ");
            for (int i = 0; i < nounList.length; i++) {
                //String noun = nounList[i].replace('_', ' ');
                if (!nouns.contains(nounList[i]))
                    nouns.put(nounList[i], new LinkedList<>());
                nouns.get(nounList[i]).add(vertex);
            }
            count++;
        }
        in.close();

        digraph = new Digraph(count);

        in = new In(hypernyms);

        while (in.hasNextLine()) {
            line = in.readLine();
            String[] vals = line.split(",");
            int v = Integer.parseInt(vals[0]);
            for (int i = 1; i < vals.length; i++)
                digraph.addEdge(v, Integer.parseInt(vals[i]));
        }

        DirectedCycle cycle = new DirectedCycle(digraph);

        if (cycle.hasCycle())
            throw new IllegalArgumentException();

        int root = -1;
        for (int v = 0; v < digraph.V(); v++) {
            if (digraph.outdegree(v) == 0) {
                root = v;
                break;
            }
        }

        if (root == -1)
            throw new IllegalArgumentException();

        if (!isAllVerticesConnected(root))
            throw new IllegalArgumentException();

        sap = new SAP(digraph);

    }

    private boolean isAllVerticesConnected(int root) {
        boolean[] marked = new boolean[digraph.V()];
        Digraph reversed = new Digraph(digraph.V());
        for (int v = 0; v < digraph.V(); v++) {
            for (int w : digraph.adj(v))
                reversed.addEdge(w, v);
        }

        dfs(reversed, root, marked);

        for (int v = 0; v < marked.length; v++)
            if (!marked[v])
                return false;

        return true;
    }

    private void dfs(Digraph graph, int v, boolean[] marked) {
        marked[v] = true;

        for (int w : graph.adj(v)) {
            if (!marked[w])
                dfs(graph, w, marked);
        }

    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keys();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();

        return nouns.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        //SAP sap = new SAP(digraph);
        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        //SAP sap = new SAP(digraph);
        int ancestor = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        return synsetsData.get(ancestor);
    }

    // do unit testing of this class
    public static void main(String[] args) {

    }
}


























