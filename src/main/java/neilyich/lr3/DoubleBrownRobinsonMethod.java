package neilyich.lr3;

import neilyich.SimpleBrownRobinsonMethod;
import org.ejml.simple.SimpleMatrix;

public class DoubleBrownRobinsonMethod extends SimpleBrownRobinsonMethod<Double> {

    private final double maxE;

    public DoubleBrownRobinsonMethod(SimpleMatrix c, double maxE) {
        super(c);
        this.maxE = maxE;
    }

    @Override
    protected boolean continueIfLimitNotReached() {
        return e(currentStep()) >= maxE;
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
