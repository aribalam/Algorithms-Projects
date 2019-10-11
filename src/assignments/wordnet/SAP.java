package assignments.wordnet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedList;

public class SAP {

    private final Digraph graph;
    private static boolean[] markedA;
    private static boolean[] markedB;
    private static int[] pathLengthA;
    private static int[] pathLengthB;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException();
        graph = new Digraph(G.V());
        for (int v = 0; v < graph.V(); v++) {
            for (int w : G.adj(v))
                graph.addEdge(v, w);
        }
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (!isVertexInRange(v) || !isVertexInRange(w))
            throw new IllegalArgumentException();

        LinkedList<Integer> listA = new LinkedList<>();
        listA.add(v);
        LinkedList<Integer> listB = new LinkedList<>();
        listB.add(w);
        return length(listA, listB);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (!isVertexInRange(v) || !isVertexInRange(w))
            throw new IllegalArgumentException();

        LinkedList<Integer> listA = new LinkedList<>();
        listA.add(v);
        LinkedList<Integer> listB = new LinkedList<>();
        listB.add(w);
        return ancestor(listA, listB);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();

        int commonAncestor = ancestor(v, w);
        if (commonAncestor == -1)
            return -1;

        return pathLengthA[commonAncestor] + pathLengthB[commonAncestor];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException();

        markedA = new boolean[graph.V()];
        markedB = new boolean[graph.V()];
        pathLengthA = new int[graph.V()];
        pathLengthB = new int[graph.V()];

        for (int i = 0; i < graph.V(); i++) {
            pathLengthB[i] = -1;
            pathLengthA[i] = -1;
        }

        Queue<Integer> queueA = new Queue<>();
        Queue<Integer> queueB = new Queue<>();

        for (int i : v) {
            if (!isVertexInRange(i))
                throw new IllegalArgumentException();
            queueA.enqueue(i);
            pathLengthA[i] = 0;
        }
        for (int i : w) {
            if (!isVertexInRange(i))
                throw new IllegalArgumentException();
            queueB.enqueue(i);
            pathLengthB[i] = 0;
        }

        while (!queueA.isEmpty()) {
            int vertex = queueA.dequeue();
            markedA[vertex] = true;

            for (int neighbour : graph.adj(vertex)) {
                if (!markedA[neighbour] && pathLengthA[neighbour] == -1) {
                    pathLengthA[neighbour] = pathLengthA[vertex] + 1;
                    queueA.enqueue(neighbour);
                }
            }
        }

        Queue<Integer> ancestors = new Queue<>();
        while (!queueB.isEmpty()) {
            int vertex = queueB.dequeue();
            markedB[vertex] = true;

            if (markedA[vertex])
                ancestors.enqueue(vertex);

            for (int neighbour : graph.adj(vertex)) {
                if (!markedB[neighbour] && pathLengthB[neighbour] == -1) {
                    pathLengthB[neighbour] = pathLengthB[vertex] + 1;
                    queueB.enqueue(neighbour);
                }
            }
        }

        int minAncestor = -1;
        int minPathlength = graph.V();
        for (int vertex : ancestors) {
            int pathLength = pathLengthA[vertex] + pathLengthB[vertex];
            if (pathLength < minPathlength) {
                minPathlength = pathLength;
                minAncestor = vertex;
            }
        }

        return minAncestor;
    }

    private boolean isVertexInRange(int v) {
        return v >= 0 && v < graph.V();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}


























