package neilyich;

import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public abstract class SimpleBrownRobinsonMethod<T> implements BrownRobinsonMethod<T> {
    private final SimpleMatrix c;

    private final int xStrategiesCount;
    private final int yStrategiesCount;
    private final double[] xWin;
    private final double[] yWin;

    private final int[] xStrategiesChoices;
    private final int[] yStrategiesChoices;

    private int k = 0;

    private final List<Integer> xStrategiesSequence = new LinkedList<>();
    private final List<List<Double>> xWinSequence = new LinkedList<>();
    private final List<Integer> yStrategiesSequence = new LinkedList<>();
    private final List<List<Double>> yWinSequence = new LinkedList<>();

    private final List<Double> minMaxCosts = new LinkedList<>();
    private final List<Double> maxMinCosts = new LinkedList<>();


    public SimpleBrownRobinsonMethod(SimpleMatrix c) {
        this.c = c;
        xStrategiesCount = c.getNumRows();
        yStrategiesCount = c.getNumCols();
        xWin = new double[xStrategiesCount];
        yWin = new double[yStrategiesCount];
        xStrategiesChoices = new int[xStrategiesCount];
        yStrategiesChoices = new int[yStrategiesCount];
    }

    @Override
    public int currentStep() {
        return k - 1;
    }

    @Override
    public int solve(int stepsLimit) {
        prepareFirstIteration();
        do {
            k++;
            makeIteration();
        } while (k < stepsLimit && continueIfLimitNotReached());
        return k;
    }

    protected void prepareFirstIteration() {
        submitStrategies(chooseXStrategy(), chooseYStrategy());
    }

    protected void makeIteration() {
        int xStrategy = chooseXStrategy();
        int yStrategy = chooseYStrategy();
        updateEstimatedCosts(xStrategy, yStrategy);
        submitStrategies(xStrategy, yStrategy);
    }

    protected abstract boolean continueIfLimitNotReached();

    protected void submitStrategies(int xStrategy, int yStrategy) {
        xStrategiesChoices[xStrategy] += 1;
        for (int yi = 0; yi < yStrategiesCount; yi++) {
            yWin[yi] += c.get(xStrategy, yi);
        }
        yStrategiesChoices[yStrategy] += 1;
        for (int xi = 0; xi < xStrategiesCount; xi++) {
            xWin[xi] += c.get(xi, yStrategy);
        }
        xStrategiesSequence.add(xStrategy);
        xWinSequence.add(Arrays.stream(xWin).boxed().toList());
        yStrategiesSequence.add(yStrategy);
        yWinSequence.add(Arrays.stream(yWin).boxed().toList());
    }

    protected void updateEstimatedCosts(int xStrategy, int yStrategy) {
        double minMaxCost = xWin[xStrategy] / (currentStep() + 1);
        if (minMaxCosts.isEmpty()) {
            minMaxCosts.add(minMaxCost);
        } else {
            minMaxCosts.add(Math.min(minMaxCost, minMaxCosts.get(minMaxCosts.size() - 1)));
        }
        double maxMinCost = yWin[yStrategy] / (currentStep() + 1);
        if (maxMinCosts.isEmpty()) {
            maxMinCosts.add(maxMinCost);
        } else {
            maxMinCosts.add(Math.max(maxMinCost, maxMinCosts.get(maxMinCosts.size() - 1)));
        }
    }

    protected int chooseXStrategy() {
        int xStrategy = 0;
        double maxWin = xWin[xStrategy];
        for (int xi = 0; xi < xStrategiesCount; xi++) {
            if (maxWin < xWin[xi]) {
                maxWin = xWin[xi];
                xStrategy = xi;
            }
        }
        return xStrategy;
    }

    protected int chooseYStrategy() {
        int yStrategy = 0;
        double minLoss = yWin[yStrategy];
        for (int yi = 0; yi < yStrategiesCount; yi++) {
            if (minLoss > yWin[yi]) {
                minLoss = yWin[yi];
                yStrategy = yi;
            }
        }
        return yStrategy;
    }

    @Override
    public int xStrategiesCount() {
        return xStrategiesCount;
    }

    @Override
    public int yStrategiesCount() {
        return yStrategiesCount;
    }

    @Override
    public int xStrategyIndex(int step) {
        return xStrategiesSequence.get(step);
    }

    @Override
    public int yStrategyIndex(int step) {
        return yStrategiesSequence.get(step);
    }

    @Override
    public List<Double> xWin(int step) {
        return xWinSequence.get(step);
    }

    @Override
    public List<Double> yWin(int step) {
        return yWinSequence.get(step);
    }

    @Override
    public List<Double> xMixedStrategy() {
        var sum = Arrays.stream(xStrategiesChoices).sum();
        return Arrays.stream(xStrategiesChoices).mapToDouble(it -> (double) it / (double) sum).boxed().toList();
    }

    @Override
    public List<Double> yMixedStrategy() {
        var sum = Arrays.stream(yStrategiesChoices).sum();
        return Arrays.stream(yStrategiesChoices).mapToDouble(it -> (double) it / (double) sum).boxed().toList();
    }

    @Override
    public double minMaxCost(int step) {
        return minMaxCosts.get(step);
    }

    @Override
    public double maxMinCost(int step) {
        return maxMinCosts.get(step);
    }
}
