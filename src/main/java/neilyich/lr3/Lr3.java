package neilyich.lr3;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.io.File;
import java.io.IOException;

import static neilyich.FormattedPrinter.*;

public class Lr3 {
    private static final ObjectMapper objectMapper = configMapper();

    public static void main(String[] args) throws IOException {
        var config = objectMapper.readValue(new File("lr3.json"), Lr3Configuration.class);
        solveUsingAnalyticSolution(config.h(), config.analyticSolution());
        System.out.println("\n".repeat(3));
        new NumericSolution(config.numericSolution()).solve(config.h());
    }

    private static void solveUsingAnalyticSolution(H h, Lr3Configuration.AnalyticSolutionConfiguration config) {
        System.out.println("Аналитический метод:");


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
        println("     x=", x);
        println("     y=", y);
        println("H(x,y)=", h.at(x, y));
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
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        return objectMapper;
    }
}
