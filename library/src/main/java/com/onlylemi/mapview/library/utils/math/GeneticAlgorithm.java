package com.onlylemi.mapview.library.utils.math;

import java.util.Arrays;
import java.util.Random;

/**
 * GeneticAlgorithm
 *
 * @author: onlylemi
 */
public class GeneticAlgorithm {

    private static final float DEFAULT_CROSSOVER_PROBABILITY = 0.9f; // 默认交叉概率
    private static final float DEFAULT_MUTATION_PROBABILITY = 0.01f; // 默认突变概率
    private static final int DEFAULT_POPULATION_SIZE = 30; // 默认种群数量
    private static final int PREVIOUS = 0;
    private static final int NEXT = 1;

    private float crossoverProbability = DEFAULT_CROSSOVER_PROBABILITY; // 交叉概率
    private float mutationProbability = DEFAULT_MUTATION_PROBABILITY; // 突变概率

    private int populationSize = DEFAULT_POPULATION_SIZE; // 种群数量
    private int mutationTimes = 0; // 变异次数
    private int currentGeneration = 0; // 当前的一代

    private int maxGeneration = 1000; // 最大代数
    private int pointNum;
    private int[][] population; // 种群集

    private float[][] dist; // 点集间的邻接矩阵
    private int[] bestIndivial; // 最短的结果集
    private float bestDist; // 最短的距离
    private int currentBestPosition; // 当前最好个体的位置

    private float currentBestDist; // 当前最好个体的距离
    private float[] values; // 种群中每个个体的dist
    private float[] fitnessValues; // 适应度集

    private float[] roulette;

    private boolean isAutoNextGeneration = false;

    private static Random rd;

    public static GeneticAlgorithm getInstance() {
        return GeneticAlgorithmHolder.instance;
    }

    private static class GeneticAlgorithmHolder {
        private static GeneticAlgorithm instance = new GeneticAlgorithm();
    }

    /**
     * 点集间的邻接矩阵
     *
     * @param matrix
     * @return
     */
    public int[] tsp(float[][] matrix) {
        this.dist = matrix;
        pointNum = matrix.length;
        init();

        if (isAutoNextGeneration) {
            int i = 0;
            while (i++ < maxGeneration) {
                nextGeneration();
            }
        }
        isAutoNextGeneration = false;

        return getBestIndivial();
    }

    /**
     * 初始化
     */
    private void init() {
        mutationTimes = 0;
        currentGeneration = 0;
        bestIndivial = null;
        bestDist = 0;
        currentBestPosition = 0;
        currentBestDist = 0;

        values = new float[populationSize];
        fitnessValues = new float[populationSize];
        roulette = new float[populationSize];
        population = new int[populationSize][pointNum];

        //initDist(points);
        // 父代
        for (int i = 0; i < populationSize; i++) {
            population[i] = randomIndivial(pointNum);
        }
        evaluateBestIndivial();
    }

    /**
     * 下一代
     */
    public int[] nextGeneration() {
        currentGeneration++;

        // 选择
        selection();
        // 交叉
        crossover();
        // 变异
        mutation();
        // 评价最好
        evaluateBestIndivial();

        return getBestIndivial();
    }

    /**
     * 选择
     */
    private void selection() {
        int[][] parents = new int[populationSize][pointNum];

        int initnum = 4;
        parents[0] = population[currentBestPosition]; // 当前种群中最好的个体
        parents[1] = exchangeMutate(bestIndivial.clone()); // 对最好的个体进行交换变异
        parents[2] = insertMutate(bestIndivial.clone()); // 对最好的个体进行插入变异
        parents[3] = bestIndivial.clone(); // 所有代中最好的个体

        setRoulette();
        for (int i = initnum; i < populationSize; i++) {
            parents[i] = population[wheelOut((int) Math.random())];
        }
        population = parents;
    }

    /**
     *
     */
    private void setRoulette() {
        //calculate all the fitness
        for (int i = 0; i < values.length; i++) {
            fitnessValues[i] = 1.0f / values[i]; // 适应度为路径长的导数
        }

        //set the roulette
        float sum = 0;
        for (int i = 0; i < fitnessValues.length; i++) {
            sum += fitnessValues[i];
        }
        for (int i = 0; i < roulette.length; i++) {
            roulette[i] = fitnessValues[i] / sum;
        }
        for (int i = 1; i < roulette.length; i++) {
            roulette[i] += roulette[i - 1];
        }
    }

    /**
     * 模拟转盘，进行子代选取
     *
     * @param ran
     * @return
     */
    private int wheelOut(int ran) {
        for (int i = 0; i < roulette.length; i++) {
            if (ran <= roulette[i]) {
                return i;
            }
        }
        return 0;
    }


    /**
     * 交换变异
     *
     * @param seq
     * @return
     */
    private int[] exchangeMutate(int[] seq) {
        mutationTimes++;
        int m, n;
        do {
            m = random(seq.length - 2);
            n = random(seq.length);
        } while (m >= n);

        int j = (n - m + 1) >> 1;
        for (int i = 0; i < j; i++) {
            int tmp = seq[m + i];
            seq[m + i] = seq[n - i];
            seq[n - i] = tmp;
        }
        return seq;
    }

    /**
     * 插入变异
     *
     * @param seq
     * @return
     */
    private int[] insertMutate(int[] seq) {
        mutationTimes++;
        int m, n;
        do {
            m = random(seq.length >> 1);
            n = random(seq.length);
        } while (m >= n);

        int[] s1 = Arrays.copyOfRange(seq, 0, m);
        int[] s2 = Arrays.copyOfRange(seq, m, n);

        for (int i = 0; i < m; i++) {
            seq[i + n - m] = s1[i];
        }
        for (int i = 0; i < n - m; i++) {
            seq[i] = s2[i];
        }
        return seq;
    }

    /**
     * 交叉
     */
    private void crossover() {
        int[] queue = new int[populationSize];
        int num = 0;
        for (int i = 0; i < populationSize; i++) {
            if (Math.random() < crossoverProbability) {
                queue[num] = i;
                num++;
            }
        }
        queue = Arrays.copyOfRange(queue, 0, num);
        queue = shuffle(queue);
        for (int i = 0; i < num - 1; i += 2) {
            doCrossover(queue[i], queue[i + 1]);
        }
    }

    private void doCrossover(int x, int y) {
        population[x] = getChild(x, y, PREVIOUS);
        population[y] = getChild(x, y, NEXT);
    }

    /**
     * 根据父代求子代
     *
     * @param x
     * @param y
     * @param pos
     * @return
     */
    private int[] getChild(int x, int y, int pos) {
        int[] solution = new int[pointNum];
        int[] px = population[x].clone();
        int[] py = population[y].clone();

        int dx = 0, dy = 0;
        int c = px[random(px.length)];
        solution[0] = c;

        for (int i = 1; i < pointNum; i++) {
            int posX = indexOf(px, c);
            int posY = indexOf(py, c);

            if (pos == PREVIOUS) {
                dx = px[(posX + px.length - 1) % px.length];
                dy = py[(posY + py.length - 1) % py.length];
            } else if (pos == NEXT) {
                dx = px[(posX + px.length + 1) % px.length];
                dy = py[(posY + py.length + 1) % py.length];
            }

            for (int j = posX; j < px.length - 1; j++) {
                px[j] = px[j + 1];
            }
            px = Arrays.copyOfRange(px, 0, px.length - 1);
            for (int j = posY; j < py.length - 1; j++) {
                py[j] = py[j + 1];
            }
            py = Arrays.copyOfRange(py, 0, py.length - 1);

            c = dist[c][dx] < dist[c][dy] ? dx : dy;

            solution[i] = c;
        }
        return solution;
    }

    /**
     * 变异
     */
    private void mutation() {
        for (int i = 0; i < populationSize; i++) {
            if (Math.random() < mutationProbability) {
                if (Math.random() > 0.5) {
                    population[i] = insertMutate(population[i]);
                } else {
                    population[i] = exchangeMutate(population[i]);
                }
                i--;
            }
        }
    }

    /**
     * 评估最好的个体
     */
    private void evaluateBestIndivial() {
        for (int i = 0; i < population.length; i++) {
            values[i] = calculateIndivialDist(population[i]);
        }
        evaluateBestCurrentDist();
        if (bestDist == 0 || bestDist > currentBestDist) {
            bestDist = currentBestDist;
            bestIndivial = population[currentBestPosition].clone();
        }
    }

    /**
     * 计算个体的距离
     *
     * @return
     */
    private float calculateIndivialDist(int[] indivial) {
        float sum = dist[indivial[0]][indivial[indivial.length - 1]];
        for (int i = 1; i < indivial.length; i++) {
            sum += dist[indivial[i]][indivial[i - 1]];
        }
        return sum;
    }

    /**
     * 评估得到最短距离
     */
    public void evaluateBestCurrentDist() {
        currentBestDist = values[0];
        for (int i = 1; i < populationSize; i++) {
            if (values[i] < currentBestDist) {
                currentBestDist = values[i];
                currentBestPosition = i;
            }
        }
    }


    /**
     * 产生个体（乱序）
     *
     * @param n
     * @return
     */
    private int[] randomIndivial(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
        }

        return shuffle(a);
    }

    /**
     * 乱序处理
     *
     * @param a
     * @return
     */
    private int[] shuffle(int[] a) {
        for (int i = 0; i < a.length; i++) {
            int p = random(a.length);
            int tmp = a[i];
            a[i] = a[p];
            a[p] = tmp;
        }
        return a;
    }

    private int random(int n) {
        Random ran = rd;
        if (ran == null) {
            ran = new Random();
        }
        return ran.nextInt(n);
    }

    private int[] concatAllArray(int[] first, int[]... rest) {
        int totalLength = first.length;
        for (int[] array : rest) {
            totalLength += array.length;
        }
        int[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (int[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    private int indexOf(int[] a, int index) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] == index) {
                return i;
            }
        }
        return 0;
    }

    public int[] getBestIndivial() {
        int[] best = new int[bestIndivial.length];
        int pos = indexOf(bestIndivial, 0);

        for (int i = 0; i < best.length; i++) {
            best[i] = bestIndivial[(i + pos) % bestIndivial.length];
        }
        return best;
    }

    public float getBestDist() {
        return bestDist;
    }

    public void setMaxGeneration(int maxGeneration) {
        this.maxGeneration = maxGeneration;
    }

    public void setAutoNextGeneration(boolean autoNextGeneration) {
        isAutoNextGeneration = autoNextGeneration;
    }

    public int getMutationTimes() {
        return mutationTimes;
    }

    public int getCurrentGeneration() {
        return currentGeneration;
    }
}
