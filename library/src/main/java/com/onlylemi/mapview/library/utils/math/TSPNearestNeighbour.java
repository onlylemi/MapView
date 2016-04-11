package com.onlylemi.mapview.library.utils.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TSPNearestNeighbour {

	private static final int INF = Integer.MAX_VALUE; // 无边

	private int numberOfNodes;
	private Stack<Integer> stack;
	private List<Integer> list;

	public TSPNearestNeighbour() {
		stack = new Stack<Integer>();
		list = new ArrayList<Integer>();
	}

	public List<Integer> tsp(int adjacencyMatrix[][]) {
		numberOfNodes = adjacencyMatrix[0].length;
		int[] visited = new int[numberOfNodes];
		visited[0] = 1;
		stack.push(0);
		int element, dst = 0, i;
		int min = Integer.MAX_VALUE;
		boolean minFlag = false;
		// System.out.print(0 + "\t");
		list.add(0);

		while (!stack.isEmpty()) {
			element = stack.peek();
			i = 0;
			min = INF;
			while (i < numberOfNodes) {
				if (adjacencyMatrix[element][i] < INF && visited[i] == 0) {
					if (min > adjacencyMatrix[element][i]) {
						min = adjacencyMatrix[element][i];
						dst = i;
						minFlag = true;
					}
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
