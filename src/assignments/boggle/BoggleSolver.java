package assignments.boggle;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.TST;

public class BoggleSolver {

    private class BoardPos {
        private final int i, j;
        private Bag<BoardPos> adjacent;
        
        public BoardPos(int i, int j) {
            this.i = i;
            this.j = j;
            adjacent = new Bag<>();
        }
        
        public void addPos(BoardPos pos) {
            adjacent.add(pos);
        }
        
        private Iterable<BoardPos> adj() {
            return adjacent;
        }

        @Override
        public String toString() {
            return "(" + i + ", " + j + ")";
        }
    }

    private final TST<Integer> dictSet;
    private BoardPos[][] boardPos;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        dictSet = new TST<>();
        for (String s : dictionary) {
            int len = s.length();
            int score = 0;
            if (len >= 3 && len <= 4)
                score++;
            else if (len > 7)
                score = 11;
            else if (len == 7)
                score = 5;
            else if (len > 2)
                score = len - 3;

            dictSet.put(s, score);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        boardPos = computeAdjacents(board);
        SET<String> keys = new SET<>();
        boolean[][] marked = new boolean[board.rows()][board.cols()];
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                String prefix = getChar(board.getLetter(i, j));
                getKeys(keys, board, prefix, boardPos[i][j], marked);
            }
        }

        return keys;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (dictSet.contains(word))
            return dictSet.get(word);
        return 0;
    }

    private void getKeys(SET<String> list, BoggleBoard board, String prefix, BoardPos pos, boolean[][] marked) {
        int i = pos.i;
        int j = pos.j;
        marked[i][j] = true;
        if (dictSet.contains(prefix) && prefix.length() > 2)
            list.add(prefix);
        if (!dictSet.keysWithPrefix(prefix).iterator().hasNext()) {
            marked[i][j] = false;
            return;
        }
        for (BoardPos nextPos : boardPos[i][j].adj()) {
            if (!marked[nextPos.i][nextPos.j])
                getKeys(list, board, prefix + getChar(board.getLetter(nextPos.i, nextPos.j)), nextPos, marked);
        }
        marked[i][j] = false;
    }
    private BoardPos[][] computeAdjacents(BoggleBoard board) {
        BoardPos[][] pos = new BoardPos[board.rows()][board.cols()];
        int m = board.rows(), n = board.cols();
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                pos[i][j] = new BoardPos(i, j);
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                BoardPos curr = pos[i][j];
                if (i > 0 && j > 0)
                    curr.addPos(pos[i-1][j-1]);
                if (i > 0 && j < n - 1)
                    curr.addPos(pos[i-1][j+1]);
                if (j > 0 && i < m - 1)
                    curr.addPos(pos[i+1][j-1]);
                if (i < m - 1 && j < n - 1)
                    curr.addPos(pos[i+1][j+1]);
                if (i > 0)
                    curr.addPos(pos[i-1][j]);
                if (j > 0)
                    curr.addPos(pos[i][j-1]);
                if (i < m - 1)
                    curr.addPos(pos[i+1][j]);
                if (j < n - 1)
                    curr.addPos(pos[i][j+1]);
            }
        }

        return pos;
    }
    private String getChar(char c) {
        return ((c == 'Q')? "QU" : c) + "";
    }

    public static void main(String[] args) {
        BoggleBoard board1 = new BoggleBoard("/media/arib/D/Java Projects/AlgorithmsII/src/assignments/boggle/board-points4.txt");
        System.out.println("Board 1 : " + board1);
        BoggleBoard board2 = new BoggleBoard("/media/arib/D/Java Projects/AlgorithmsII/src/assignments/boggle/board-q.txt");
        System.out.println("Board 1 : " + board2);
        In in = new In("/media/arib/D/Java Projects/AlgorithmsII/src/assignments/boggle/dictionary-yawl.txt");
        String[] dict = in.readAllStrings();
        BoggleSolver bs = new BoggleSolver(dict);
        System.out.println("Valid words from board 1 : ");
        int score = 0;
        for (String s : bs.getAllValidWords(board1)) {
            System.out.println(s);
            score += bs.scoreOf(s);
        }
        System.out.println("Final Score : " + score);
        System.out.println("Valid words from board 2 : ");
        score = 0;
        for (String s : bs.getAllValidWords(board2)) {
            System.out.println(s);
            score += bs.scoreOf(s);
        }
        System.out.println("Final Score : " + score);
    }


}