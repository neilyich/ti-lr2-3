package neilyich.lr3;

import neilyich.SimpleBrownRobinsonMethod;
import org.ejml.simple.SimpleMatrix;

import java.util.Deque;
import java.util.LinkedList;

public class DoubleBrownRobinsonMethod extends SimpleBrownRobinsonMethod<Double> {

    private final Deque<Double> lastResults = new LinkedList<>();

    private final H h;
    private final int lastResultsCount;
    private final double maxE;

    public DoubleBrownRobinsonMethod(H h, SimpleMatrix c, int lastResultsCount, double maxE) {
        super(c);
        this.h = h;
        this.lastResultsCount = lastResultsCount;
        this.maxE = maxE;
    }

    @Override
    protected void makeIteration() {
        super.makeIteration();
        if (lastResults.size() == lastResultsCount) {
            lastResults.removeFirst();
        }
        lastResults.addLast(h.at(dominantXStrategy(), dominantYStrategy()));
    }

    @Override
    protected boolean continueIfLimitNotReached() {
        if (lastResults.size() < lastResultsCount) {
            return true;
        }
        double maxH = lastResults.stream().mapToDouble(it -> it).max().orElseThrow();
        double minH = lastResults.stream().mapToDouble(it -> it).min().orElseThrow();
        return maxH - minH < maxE;
    }

    @Override
    public Double xStrategyByIndex(int index) {
        return (double) index / (xStrategiesCount() - 1);
    }

    @Override
    public Double yStrategyByIndex(int index) {
        return (double) index / (yStrategiesCount() - 1);
    }
}
