package assignments.burrowswheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.Queue;

public class BurrowsWheeler {

    private static final int RADIX = 256;

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        // Construct a circular suffix array
        String str = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(str);
        int length = csa.length();
        // get original index of suffix to retrieve first character
        // and then the last character of the suffixes
        char[] t = new char[length];
        for (int i = 0; i < length; i++) {
            int index = csa.index(i);
            if (index == 0)
                BinaryStdOut.write(i, 32);
            int lastInd = (index + length - 1) % length;
            t[i] = str.charAt(lastInd);
        }

        for (int i = 0; i < length; i++)
            BinaryStdOut.write(t[i], 8);

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        int index = BinaryStdIn.readInt();
        char[] t = BinaryStdIn.readString().toCharArray();

        // Make an array of Queue and add character indices to it
        Queue<Integer>[] lists = new Queue[RADIX];
        for (int i = 0; i < t.length; i++) {
            if (lists[t[i]] == null)
                lists[t[i]] = new Queue<>();
            lists[t[i]].enqueue(i);
        }

        // create a next array and add those indices sequencially (alphabet wise)
        int[] next = new int[t.length];
        int offset = 0;
        for (int r = 0; r < RADIX; r++) {
            if (lists[r] != null) {
                while (!lists[r].isEmpty())
                    next[offset++] = lists[r].dequeue();
            }
        }

        // stores the final decoded string
        char[] string = new char[t.length];
        for (int i = 0; i < next.length; i++) {
            char ch = t[next[index]];
            string[i] = ch;
            index = next[index];
        }

        BinaryStdOut.write(new String(string));
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args == null)
            throw new IllegalArgumentException();
        if (args[0].equals("-")) {
            BurrowsWheeler.transform();
        }
        else if (args[0].equals("+")) {
            BurrowsWheeler.inverseTransform();
        }
        else
            throw new IllegalArgumentException();
    }
}
