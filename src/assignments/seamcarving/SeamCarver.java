package assignments.seamcarving;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {

    private int height;
    private int width;
    private double[][] energyMatrix;
    private Color[][] color;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException();

        height = picture.height();
        width = picture.width();
        energyMatrix = new double[height][width];
        color = new Color[height][width];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                color[y][x] = picture.get(x, y);
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                energyMatrix[y][x] = findEnergy(x, y);
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width, height);
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                picture.set(col, row, color[row][col]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height)
            throw new IllegalArgumentException();
        return energyMatrix[y][x];
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] energySum = new double[height][width];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                energySum[y][x] = Double.POSITIVE_INFINITY;

        for (int y = 0; y < height; y++)
            energySum[y][0] = energyMatrix[y][0];

        for (int x = 0; x < width-1; x++) {
            for (int y = 0; y < height; y++) {
                for (int nextY : getAdjacentHorizontal(y))
                    relaxEdges(energySum, x, y, x+1, nextY);
            }
        }

        int[] seam = new int[width];
        double minEnergySum = Double.POSITIVE_INFINITY;
        for (int y = 0; y < height; y++) {
            if (energySum[y][width-1] < minEnergySum) {
                minEnergySum = energySum[y][width-1];
                seam[width - 1] = y;
            }
        }

        for (int x = width-2; x >= 0; x--) {
            minEnergySum = Double.POSITIVE_INFINITY;
            for (int nextY : getAdjacentHorizontal(seam[x+1])) {
                if (energySum[nextY][x] < minEnergySum) {
                    minEnergySum = energySum[nextY][x];
                    seam[x] = nextY;
                }
            }
        }

        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] energySum = new double[height][width];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                energySum[y][x] = Double.POSITIVE_INFINITY;

        for (int x = 0; x < width; x++)
            energySum[0][x] = energyMatrix[0][x];

        for (int y = 0; y < height-1; y++) {
            for (int x = 0; x < width; x++) {
                for (int nextX : getAdjacentVertical(x))
                    relaxEdges(energySum, x, y, nextX, y+1);
            }
        }

        int[] seam = new int[height];
        double minEnergySum = Double.POSITIVE_INFINITY;
        for (int x = 0; x < width; x++) {
            if (energySum[height - 1][x] < minEnergySum) {
                minEnergySum = energySum[height - 1][x];
                seam[height - 1] = x;
            }
        }

        for (int y = height-2; y >= 0; y--) {
            minEnergySum = Double.POSITIVE_INFINITY;
            for (int nextX : getAdjacentVertical(seam[y+1])) {
                if (energySum[y][nextX] < minEnergySum) {
                    minEnergySum = energySum[y][nextX];
                    seam[y] = nextX;
                }
            }
        }

        return seam;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (seam.length != width)
            throw new IllegalArgumentException();
        for (int i = 0; i < width; i++) {
            if (seam[i] < 0 || seam[i] >= height)
                throw new IllegalArgumentException();
            if (i > 0 && Math.abs(seam[i] - seam[i-1]) > 1)
                throw new IllegalArgumentException();
        }

        // To check if new transpose matrix is needed or not
        double[][] energyMatrixTranspose = transpose(energyMatrix);
        Color[][] colorTranspose = transpose(color);

        // Shift the elements on the transposed matrix
        shiftElements(energyMatrixTranspose, height, width, seam);
        shiftElements(colorTranspose, height, width, seam);

        // Update height and the new matrices;
        energyMatrix = transpose(energyMatrixTranspose);
        color = transpose(colorTranspose);
        height--;

        // Update energies
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                energyMatrix[y][x] = findEnergy(x, y);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        if (seam == null)
            throw new IllegalArgumentException();
        if (seam.length != height)
            throw new IllegalArgumentException();
        for (int i = 0; i < height; i++) {
            if (seam[i] < 0 || seam[i] >= width)
                throw new IllegalArgumentException();
            if (i > 0 && Math.abs(seam[i] - seam[i-1]) > 1)
                throw new IllegalArgumentException();
        }

        shiftElements(energyMatrix, width, height, seam);
        shiftElements(color, width, height, seam);
        //update width
        width--;
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                energyMatrix[y][x] = findEnergy(x, y);
    }

    private double findEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == width()-1 || y == height()-1)
            return 1000;

        Color leftPixel = color[y][x-1], rightPixel = color[y][x+1];
        Color topPixel = color[y-1][x], bottomPixel = color[y+1][x];

        double rX = leftPixel.getRed() - rightPixel.getRed();
        double gX = leftPixel.getGreen() - rightPixel.getGreen();
        double bX = leftPixel.getBlue() - rightPixel.getBlue();

        double deltaXSqr = rX * rX + gX * gX + bX * bX;

        double rY = topPixel.getRed() - bottomPixel.getRed();
        double gY = topPixel.getGreen() - bottomPixel.getGreen();
        double bY = topPixel.getBlue() - bottomPixel.getBlue();

        double deltaYSqr = rY * rY + gY * gY + bY * bY;

        return Math.sqrt(deltaXSqr + deltaYSqr);
    }
    private Iterable<Integer> getAdjacentVertical(int x) {
        Bag<Integer> bag = new Bag<>();
        if (x > 0)
            bag.add(x-1);
        bag.add(x);
        if (x < width-1)
            bag.add(x+1);

        return bag;
    }
    private Iterable<Integer> getAdjacentHorizontal(int y) {
        Bag<Integer> bag = new Bag<>();
        if (y > 0)
            bag.add(y-1);
        bag.add(y);
        if (y < height-1)
            bag.add(y+1);

        return bag;
    }
    private double[][] transpose(double[][] matrix) {
        double[][] transpose = new double[matrix[0].length][matrix.length];
        for (int i = 0; i < transpose.length; i++)
            for (int j = 0; j < transpose[i].length; j++)
                transpose[i][j] = matrix[j][i];

        return transpose;
    }
    private Color[][] transpose(Color[][] matrix) {
        Color[][] transpose = new Color[matrix[0].length][matrix.length];
        for (int i = 0; i < transpose.length; i++)
            for (int j = 0; j < transpose[i].length; j++)
                transpose[i][j] = matrix[j][i];

        return transpose;
    }
    private void shiftElements(double[][] matrix, int width, int height, int[] seam) {
        for (int y = 0; y < height; y++) {
            int x = seam[y];
            System.arraycopy(matrix[y], x+1, matrix[y], x, width - x - 1);
        }
    }
    private void shiftElements(Color[][] matrix, int width, int height, int[] seam) {
        for (int y = 0; y < height; y++) {
            int x = seam[y];
            System.arraycopy(matrix[y], x+1, matrix[y], x, width - x - 1);
        }
    }
    private void relaxEdges(double[][] energySum, int x1, int y1, int x2, int y2) {
        if (energySum[y2][x2] > energySum[y1][x1] + energyMatrix[y2][x2])
            energySum[y2][x2] = energySum[y1][x1] + energyMatrix[y2][x2];
    }

}