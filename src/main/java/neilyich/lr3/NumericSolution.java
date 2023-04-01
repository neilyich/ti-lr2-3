package neilyich.lr3;

import org.ejml.simple.SimpleMatrix;

import java.util.LinkedList;
import java.util.Queue;

import static neilyich.FormattedPrinter.*;
import static neilyich.FormattedPrinter.println;
import static neilyich.lr3.SaddlePointFinder.findSaddlePoint;

public class NumericSolution {

    private final int maxN;
    private final int lastResultsCount;
    private final double maxE;
    private final double brownRobinsonMaxE;
    private final int brownRobinsonStepsLimit;
    private final int printWidth;
    private final int printScale;

    private record HPoint(double x, double y, double h) {}

    private final Queue<HPoint> lastResults = new LinkedList<>();

    public NumericSolution(Lr3Configuration.NumericSolutionConfiguration config) {
        this.maxN = config.maxN();
        this.lastResultsCount = config.lastResultsCount();
        this.maxE = config.maxE();
        this.brownRobinsonMaxE = config.brownRobinson().maxE();
        this.brownRobinsonStepsLimit = config.brownRobinson().stepsLimit();
        this.printWidth = config.formatting().width();
        this.printScale = config.formatting().scale();
    }

    public void solve(H h) {
        System.out.println("Численный способ:");
        PRINT_WIDTH = printWidth;
        PRINT_SCALE = printScale;
        lastResults.clear();
        int n = 2;
        for (; n <= maxN; n++) {
            System.out.println("-".repeat(30));
            System.out.println("N = " + n + ":");
            var c = buildMatrix(h, n);
            if (n <= 10) {
                printMatrix(c);
            }
            var saddlePoint = findSaddlePoint(c);
            if (saddlePoint.isPresent()) {
                System.out.println("Найдена седловая точка:");
                var x = (double) saddlePoint.get().row() / n;
                var y = (double) saddlePoint.get().col() / n;
                println("     x=", x);
                println("     y=", y);
                var hAtXY = h.at(x, y);
                submitResult(x, y, hAtXY);
                println("H(x,y)=", hAtXY);
            } else {
                var method = new DoubleBrownRobinsonMethod(c, brownRobinsonMaxE);
                method.solve(brownRobinsonStepsLimit);
                System.out.println("Седловая точка не найдена, решение методом Брауна-Робинсон:");
                var x  = method.dominantXStrategy();
                var y = method.dominantYStrategy();
                println("     x=", x);
                println("     y=", y);
                var hAtXY = h.at(x, y);
                submitResult(x, y, hAtXY);
                println("H(x,y)=", hAtXY);
            }
            if (shouldStop()) {
                break;
            }
        }
        var result = calcAvgResult(h);
        System.out.println("-".repeat(30));
        System.out.println("Получено решение, потребовалось итераций: " + n);
        println("     x=", result.x());
        println("     y=", result.y());
        println("H(x,y)=", result.h());

    }

    private SimpleMatrix buildMatrix(H h, int n) {
        var c = new SimpleMatrix(n + 1, n + 1);
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                c.set(i, j, h.at((double) i / n, (double) j / n));
            }
        }
        return c;
    }

    private void submitResult(double x, double y, double h) {
        if (lastResults.size() == lastResultsCount) {
            lastResults.remove();
        }
        lastResults.add(new HPoint(x, y, h));
    }

    private boolean shouldStop() {
        if (lastResults.size() < lastResultsCount) {
            return false;
        }
        var max = lastResults.stream().mapToDouble(HPoint::h).max().orElse(maxE + 1);
        var min = lastResults.stream().mapToDouble(HPoint::h).min().orElse(0);
        return (max - min) < maxE;
    }

    private HPoint calcAvgResult(H h) {
        var avgX = lastResults.stream().mapToDouble(HPoint::x).average().orElse(0);
        var avgY = lastResults.stream().mapToDouble(HPoint::y).average().orElse(0);
        var hAtXY = h.at(avgX, avgY);
        return new HPoint(avgX, avgY, hAtXY);
    }
}
