package neilyich;

import java.util.List;

import static neilyich.FormattedPrinter.print;
import static neilyich.FormattedPrinter.printString;

public class BrownRobinsonMethodPrinter<T extends Number> {
    private BrownRobinsonMethod<T> brm;
    public static String DELIMITER = " |";

    public void printTable(BrownRobinsonMethod<T> brm, int rows) {
        this.brm = brm;
        printHeader();
        for (int row = 0; row < rows; row++) {
            printRow(row);
        }
    }

    private void printHeader() {
        printRow(List.of(
                () -> printString("i"),
                () -> printString("Ai"),
                () -> printString("Bi"),
                () -> printGroup("A win", brm.xStrategiesCount()),
                () -> printGroup("B win", brm.yStrategiesCount()),
                () -> printString("V max"),
                () -> printString("V min"),
                () -> printString("e")
        ));
    }

    private void printRow(Iterable<Runnable> contentCreators) {
        for (var cellCreator : contentCreators) {
            cellCreator.run();
            sep();
        }
        nextLine();
    }

    private void printRow(int row) {
        printRow(List.of(
                () -> print(row + 1),
                () -> print(brm.xStrategy(row)),
                () -> print(brm.yStrategy(row)),
                () -> printGroup(brm.xWin(row)),
                () -> printGroup(brm.yWin(row)),
                () -> print(brm.maxCostEstimate(row)),
                () -> print(brm.minCostEstimate(row)),
                () -> print(brm.e(row))
        ));
    }

    private void printGroup(String content, int colsCount) {
        for (int i = 0; i < colsCount - 1; i++) {
            printString("");
        }
        printString(content);
    }

    private void printGroup(List<Double> content) {
        for (var c : content) {
            print(c);
        }
    }

    private void sep() {
        System.out.print(DELIMITER);
    }

    private void nextLine() {
        System.out.println();
    }
}
