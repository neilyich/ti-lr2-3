package neilyich.lr3;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.ejml.simple.SimpleMatrix;

import java.io.File;
import java.io.IOException;

import static neilyich.FormattedPrinter.*;
import static neilyich.lr3.SaddlePointFinder.findSaddlePoint;

public class Lr3 {
    private static final ObjectMapper objectMapper = configMapper();

    public static void main(String[] args) throws IOException {
        var config = objectMapper.readValue(new File("lr3.json"), Lr3Configuration.class);
        solveUsingAnalyticSolution(config.h(), config.analyticSolution());
        System.out.println("\n".repeat(3));
        solveUsingNumericSolution(config.h(), config.numericSolution());
    }

    private static void solveUsingAnalyticSolution(H h, Lr3Configuration.AnalyticSolutionConfiguration config) {
        System.out.println("-".repeat(30));
        System.out.println("Analytic solution");
        System.out.println("-".repeat(30));


        PRINT_WIDTH = config.formatting().width();
        PRINT_SCALE = config.formatting().scale();

        var a = h.a();
        var b = h.b();
        var c = h.c();
        var d = h.d();
        var e = h.e();

        var c2 = c*c;
        var _4ab = 4*a*b;
        var x = (-c*e/_4ab + d/(2*a)) / (c2/_4ab - 1);
        var y = (-c*d/_4ab + e/(2*b)) / (c2/_4ab - 1);
        if (x < 0) {
            System.out.println("x<0");
            x = 0;
            y = -(c*x + e) / (2*b);
        } else if (y < 0) {
            System.out.println("y<0");
            y = 0;
            x = -(c*y + d) / (2*a);
        }
        println(" X=", x);
        println(" Y=", y);
        println(" H=", h.at(x, y));
        println("Hx=", h.xAt(x, y));
        println("Hy=", h.yAt(x, y));
    }

    private static void solveUsingNumericSolution(H h, Lr3Configuration.NumericSolutionConfiguration config) {
        System.out.println("-".repeat(30));
        System.out.println("Numeric solution");
        PRINT_WIDTH = config.formatting().width();
        PRINT_SCALE = config.formatting().scale();
        int maxN = config.maxN();
        int lastResultsCount = config.lastResultsCount();
        double maxE = config.maxE();
        int stepsLimit = config.stepsLimit();
        for (int n = 2; n <= maxN; n++) {
            System.out.println("-".repeat(30));
            println("N=", n);
            var c = buildMatrix(h, n);
            var saddlePoint = findSaddlePoint(c);
            if (saddlePoint.isPresent()) {
                System.out.println("Found saddle point:");
                var x = (double) saddlePoint.get().row() / n;
                var y = (double) saddlePoint.get().col() / n;
                println("X=", x);
                println("Y=", y);
                println("H=", h.at(x, y));
            } else {
                var method = new DoubleBrownRobinsonMethod(h, c, lastResultsCount, maxE);
                method.solve(stepsLimit);
                System.out.println("No saddle point, solved using Brown-Robinson method:");
                println("X=", method.dominantXStrategy());
                println("Y=", method.dominantYStrategy());
                println("H=", h.at(method.dominantXStrategy(), method.dominantYStrategy()));
            }
        }
    }

    private static SimpleMatrix buildMatrix(H h, int n) {
        var c = new SimpleMatrix(n + 1, n + 1);
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= n; j++) {
                c.set(i, j, h.at((double) i / n, (double) j / n));
            }
        }
        printMatrix(c);
        return c;
    }

    private static ObjectMapper configMapper() {
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(double.class, new StdDeserializer<>(double.class) {
            @Override
            public Double deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
                var value = jsonParser.getValueAsString();
                try {
                    return Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    return new ExpressionBuilder(value).build().evaluate();
                }
            }
        });
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
