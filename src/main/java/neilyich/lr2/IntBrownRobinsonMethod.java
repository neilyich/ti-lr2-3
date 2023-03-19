package neilyich.lr2;

import neilyich.SimpleBrownRobinsonMethod;
import org.ejml.simple.SimpleMatrix;

public class IntBrownRobinsonMethod extends SimpleBrownRobinsonMethod<Integer> {
    private final double maxE;
    public IntBrownRobinsonMethod(double maxE, SimpleMatrix c) {
        super(c);
        this.maxE = maxE;
    }

    @Override
    protected boolean continueIfLimitNotReached() {
        return e(currentStep()) >= maxE;
    }

    @Override
    public Integer xStrategyByIndex(int index) {
        return index + 1;
    }

    @Override
    public Integer yStrategyByIndex(int index) {
        return index + 1;
    }
}
