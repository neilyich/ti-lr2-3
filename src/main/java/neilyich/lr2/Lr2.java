package neilyich.lr2;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import neilyich.BrownRobinsonConfiguration;
import neilyich.BrownRobinsonMethodPrinter;
import org.ejml.equation.Equation;
import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;
import java.util.stream.DoubleStream;

import static neilyich.FormattedPrinter.*;

public class Lr2 {

    private static final ObjectMapper objectMapper = configMapper();

    public static void main(String[] args) throws IOException {
        var c = new SimpleMatrix(new double[][]{
                {1.0, 11.0, 11.0},
                {7.0, 5.0, 8.0},
                {16.0, 6.0, 2.0}
        });

        var config = objectMapper.readValue(new File("lr2.json"), Lr2Configuration.class);

        solveUsingBrownRobinsonMethod(config.c(), config.brownRobinson());

        System.out.println("\n".repeat(4));

        solveUsingInverseMatrix(config.c(), config.inverseMatrix());
    }

    private static void solveUsingBrownRobinsonMethod(SimpleMatrix c, BrownRobinsonConfiguration config) {
        System.out.println("Метод Брауна-Робинсон:");
        PRINT_SCALE = config.formatting().scale();
        PRINT_WIDTH = config.formatting().width();
        var maxE = config.maxE();
        var stepsLimit = config.stepsLimit();
        var method = new IntBrownRobinsonMethod(maxE, c);
        var steps = method.solve(stepsLimit);
        var printer = new BrownRobinsonMethodPrinter<Integer>();
        printer.printTable(method, steps);
        System.out.println();
        System.out.print("Смешанная стратегия игрока А:");
        for (var xStrategy : method.xMixedStrategy()) {
            print(xStrategy);
        }
        System.out.println();
        System.out.print("Смешанная стратегия игрока В:");
        for (var yStrategy : method.yMixedStrategy()) {
            print(yStrategy);
        }
        System.out.println();
        print("Цена игры: ", (method.maxMinCost(steps - 1) + method.minMaxCost(steps - 1)) / 2);
    }

    private static void solveUsingInverseMatrix(SimpleMatrix c, Lr2Configuration.InverseMatrixConfiguration config) {
        System.out.println("Аналитический метод:");
        PRINT_SCALE = config.formatting().scale();
        PRINT_WIDTH = config.formatting().width();
        if (c.getNumRows() != c.getNumCols()) {
            throw new IllegalArgumentException("Matrix must be n x n");
        }
        var c1 = c.invert();
        System.out.println("Обратная матрица:");
        printMatrix(c1);
        var ut = new SimpleMatrix(DoubleStream.generate(() -> 1.0).limit(c.getNumRows()).toArray());
        var u = ut.transpose();
        var eq = new Equation();
        eq.alias(c1, "c1", u, "u", ut, "ut");
        eq.process("d = u * c1 * ut");
        eq.process("x = (u * c1) / d");
        var x = eq.lookupSimple("x");
        System.out.println();
        System.out.print("Смешанная стратегия игрока А:");
        printMatrix(x);
        eq.process("y = (c1 * ut)' / d");
        var y = eq.lookupSimple("y");
        System.out.print("Смешанная стратегия игрока В:");
        printMatrix(y);
        var v = 1 / eq.lookupDouble("d");
        System.out.print("Цена игры:");
        print(v);
    }

    private static ObjectMapper configMapper() {
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(SimpleMatrix.class, new StdDeserializer<>(SimpleMatrix.class) {
            @Override
            public SimpleMatrix deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
                var matrix = jsonParser.readValueAs(double[][].class);
                return new SimpleMatrix(matrix);
            }
        });
        objectMapper.registerModule(module);
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        return objectMapper;
    }
}