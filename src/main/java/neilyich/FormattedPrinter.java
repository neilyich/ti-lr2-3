package neilyich;

import org.ejml.simple.SimpleMatrix;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class FormattedPrinter {
    public static int PRINT_SCALE = 3;
    public static int PRINT_WIDTH = 5;

    public static void printString(String content) {
        System.out.print(" ".repeat(PRINT_WIDTH - content.length()));
        System.out.print(content);
    }
    public static void print(BigDecimal number) {
        String content = number.setScale(PRINT_SCALE, RoundingMode.HALF_EVEN).toString();
        content = content.replaceAll("\\.0+$", "");
        printString(content);
    }

    public static void print(double number) {
        print(BigDecimal.valueOf(number));
    }

    public static void print(Number number) {
        if (number instanceof Integer) {
            printString(number.toString());
        } else if (number instanceof Double) {
            print(number.doubleValue());
        }
    }

    public static void printMatrix(SimpleMatrix m) {
        for (int i = 0; i < m.getNumRows(); i++) {
            for (int j = 0; j < m.getNumCols(); j++) {
                print(m.get(i, j));
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static void println(String label, Number value) {
        System.out.print(label);
        print(value);
        System.out.println();
    }

    public static void print(String label, Number value) {
        System.out.print(label);
        print(value);
    }
}
