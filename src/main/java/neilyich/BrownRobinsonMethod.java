package neilyich;

import java.util.List;

public interface BrownRobinsonMethod<T> {
    int xStrategiesCount();
    int yStrategiesCount();
    int xStrategyIndex(int step);
    int yStrategyIndex(int step);
    List<Double> xWin(int step);
    List<Double> yWin(int step);

    List<Double> xMixedStrategy();
    List<Double> yMixedStrategy();

    double minMaxCost(int step);
    double maxMinCost(int step);

    T xStrategyByIndex(int index);
    T yStrategyByIndex(int index);

    default T xStrategy(int step) {
        return xStrategyByIndex(xStrategyIndex(step));
    }

    default T yStrategy(int step) {
        return yStrategyByIndex(yStrategyIndex(step));
    }

    int solve(int stepsLimit);

    int currentStep();

    default T dominantXStrategy() {
        return xStrategyByIndex(dominantStrategyIndex(xMixedStrategy()));
    }

    default T dominantYStrategy() {
        return yStrategyByIndex(dominantStrategyIndex(yMixedStrategy()));
    }

    default int dominantStrategyIndex(List<Double> mixedStrategy) {
        var mostPossibleStrategy = 0;
        var maxP = mixedStrategy.get(mostPossibleStrategy);
        for (int i = 1; i < mixedStrategy.size(); i++) {
            var p = mixedStrategy.get(i);
            if (p > maxP) {
                maxP = p;
                mostPossibleStrategy = i;
            }
        }
        return mostPossibleStrategy;
    }
//
//    private double strategyToDouble(int strategy, int strategiesCount) {
//        return (double) strategy / (strategiesCount - 1);
//    }


    default double xWin(int step, int xStrategy) {
        return xWin(step).get(xStrategy);
    }
    default double yWin(int step, int yStrategy) {
        return yWin(step).get(yStrategy);
    }
    default double maxCostEstimate(int step) {
        return xWin(step, xStrategyIndex(step + 1)) / (step + 1);
    }
    default double minCostEstimate(int step) {
        return yWin(step, yStrategyIndex(step + 1)) / (step + 1);
    }
    default double e(int step) {
        return minMaxCost(step) - maxMinCost(step);
    }
}
