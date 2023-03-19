package neilyich.lr3;

import org.ejml.simple.SimpleMatrix;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

public class SaddlePointFinder {

    public static Optional<SaddlePoint> findSaddlePoint(SimpleMatrix c) {
        var maxMin = maxMin(c);
        var minMax = minMax(c);
        if (maxMin.row() == minMax.row() && maxMin.col() == minMax.col()) {
            return Optional.of(maxMin);
        }
        return Optional.empty();
    }

    private static SaddlePoint minMax(SimpleMatrix c) {
        var maxs = new SaddlePoint[c.getNumCols()];
        for (int col = 0; col < c.getNumCols(); col++) {
            var max = new SaddlePoint(0, col, c.get(0, col));
            for (int row = 0; row < c.getNumRows(); row++) {
                if (c.get(row, col) > max.value()) {
                    max = new SaddlePoint(row, col, c.get(row, col));
                }
            }
            maxs[col] = max;
        }
        return Stream.of(maxs).min(Comparator.comparingDouble(SaddlePoint::value)).orElseThrow();
    }

    private static SaddlePoint maxMin(SimpleMatrix c) {
        var mins = new SaddlePoint[c.getNumCols()];
        for (int row = 0; row < c.getNumCols(); row++) {
            var min = new SaddlePoint(row, 0, c.get(row, 0));
            for (int col = 0; col < c.getNumRows(); col++) {
                if (c.get(row, col) < min.value()) {
                    min = new SaddlePoint(row, col, c.get(row, col));
                }
            }
            mins[row] = min;
        }
        return Stream.of(mins).max(Comparator.comparingDouble(SaddlePoint::value)).orElseThrow();
    }
}
