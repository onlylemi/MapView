package com.onlylemi.mapview.library.utils.math;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class TSPNearestNeighbour {

    private static final float INF = Float.MAX_VALUE;

    private int numberOfNodes;
    private Deque<Integer> stack;
    private List<Integer> list;

    public TSPNearestNeighbour() {
        stack = new ArrayDeque<>();
        list = new ArrayList<>();
    }

    public static TSPNearestNeighbour getInstance() {
        return TSPNearestNeighbourHolder.instance;
    }
    private static class TSPNearestNeighbourHolder {
        private static TSPNearestNeighbour instance = new TSPNearestNeighbour();

    }

    public List<Integer> tsp(float[][] matrix) {
        numberOfNodes = matrix[0].length;
        int[] visited = new int[numberOfNodes];
        visited[0] = 1;
        stack.push(0);
        int element, dst = 0, i;
        boolean minFlag = false;

        // System.out.print(0 + "\t");
        list.add(0);
        while (!stack.isEmpty()) {
            element = stack.peek();
            i = 0;
            float min = INF;
            while (i < numberOfNodes) {
                if (matrix[element][i] < INF && visited[i] == 0 && min > matrix[element][i]) {
                    min = matrix[element][i];
                    dst = i;
                    minFlag = true;
                }
                i++;
            }
            if (minFlag) {
                visited[dst] = 1;
                stack.push(dst);
                // System.out.print(dst + "\t");
                list.add(dst);
                minFlag = false;
                continue;
            }
            stack.pop();
        }
        return list;
    }
}
