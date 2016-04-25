package com.onlylemi.mapview.library.utils.math;

import java.util.ArrayList;
import java.util.List;

/**
 * FloydAlgorithm
 *
 * @author: onlylemi
 */
public final class FloydAlgorithm {

    private static final int INF = Integer.MAX_VALUE;
    private float[][] dist;

    // the shortest path from i to j
    private int[][] path;
    private List<Integer> result;

    public static FloydAlgorithm getInstance() {
        return FloydAlgorithmHolder.instance;
    }

    private static class FloydAlgorithmHolder {
        private static FloydAlgorithm instance = new FloydAlgorithm();
    }

    private void init(float[][] matrix) {
        dist = null;
        path = null;
        result = new ArrayList<>();

        this.dist = new float[matrix.length][matrix.length];
        this.path = new int[matrix.length][matrix.length];
    }

    /**
     * the shortest between begin to end
     *
     * @param begin
     * @param end
     * @param matrix
     */
    public List<Integer> findCheapestPath(int begin, int end, float[][] matrix) {
        init(matrix);

        floyd(matrix);
        result.add(begin);
        findPath(begin, end);
        result.add(end);

        return result;
    }

    private void findPath(int i, int j) {
        int k = path[i][j];
        if (k == -1)
            return;
        findPath(i, k); // recursion
        result.add(k);
        findPath(k, j);
    }

    private void floyd(float[][] matrix) {
        int size = matrix.length;
        // initialize dist and path
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                path[i][j] = -1;
                dist[i][j] = matrix[i][j];
            }
        }
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (dist[i][k] != INF && dist[k][j] != INF
                            && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        path[i][j] = k;
                    }
                }
            }
        }

    }

}
