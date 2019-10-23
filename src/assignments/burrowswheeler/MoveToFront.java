package assignments.burrowswheeler;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    private static final int RADIX = 256;

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // stores the index of each character in the sequence
        int[] indices = new int[RADIX];
        char[] alphabet = new char[RADIX];
        for (char r = 0; r < RADIX; r++) {
            indices[r] = r;
            alphabet[r] = r;
        }

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar(8);
            // find its index in the sequence
            int index = indices[ch];
            // write the 8-bit index in the output stream
            BinaryStdOut.write(index, 8);
            // shift the character to the front
            for (int i = index; i > 0; i--)
                exchange(indices, alphabet, i, i-1);
        }

        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // stores the index of each character in the sequence
        int[] indices = new int[RADIX];
        char[] alphabet = new char[RADIX];
        for (char r = 0; r < RADIX; r++) {
            indices[r] = r;
            alphabet[r] = r;
        }

        while (!BinaryStdIn.isEmpty()) {
            int ind = BinaryStdIn.readInt(8);
            // find the character at that index
            char ch = alphabet[ind];
            // write the 8-bit character in the output stream
            BinaryStdOut.write(ch, 8);
            // shift the character to the front
            for (int i = ind; i > 0; i--)
                exchange(indices, alphabet, i, i-1);
        }

        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args == null || (!args[0].equals("+") && !args[0].equals("-")))
            throw new IllegalArgumentException();
        if (args[0].equals("+"))
            MoveToFront.decode();
        else
            MoveToFront.encode();
    }

    private static void exchange(int[] ind, char[] chars, int i, int j) {
        char c = chars[i];
        chars[i] = chars[j];
        chars[j] = c;
        ind[chars[i]] = i;
        ind[chars[j]] = j;
    }
}
