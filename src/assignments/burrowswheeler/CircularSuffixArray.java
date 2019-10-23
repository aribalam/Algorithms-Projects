package assignments.burrowswheeler;

import edu.princeton.cs.algs4.Merge;;
import java.util.Scanner;

public class CircularSuffixArray {

    private int length;
    private Node[] indices;

    private static class Node implements Comparable<Node> {

        private static String str;
        private int i;

        private Node(int i) {
            this.i = i;
        }

        @Override
        public int compareTo(Node that) {
            int count = 0;
            for (int i = this.i, j = that.i; count < str.length(); count++) {
                if (str.charAt(i) < str.charAt(j))
                    return -1;
                else if (str.charAt(i) > str.charAt(j))
                    return 1;
                i = (i+1) % str.length();
                j = (j+1) % str.length();
            }
            return 0;
        }
    }

    public CircularSuffixArray(String s) {    // circular suffix array of s
        if (s == null)
            throw new IllegalArgumentException();
        length = s.length();
        Node.str = s;

        indices = new Node[length];
        for (int i = 0; i < indices.length; i++)
            indices[i] = new Node(i);

        Merge.sort(indices);
    }

    public int length() {                     // length of s
        return length;
    }

    public int index(int i) {                 // returns index of ith sorted suffix
        if (i < 0 || i >= length)
            throw new IllegalArgumentException();
        return indices[i].i;
    }

    public static void main(String[] args) {  // unit testing (required)
        Scanner sc = new Scanner(System.in);
        CircularSuffixArray c = new CircularSuffixArray(sc.nextLine());

        for (int i = 0; i < c.length(); i++)
            System.out.println(c.index(i));
    }
}

