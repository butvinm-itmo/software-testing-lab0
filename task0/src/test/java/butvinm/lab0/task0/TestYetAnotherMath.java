package butvinm.lab0.task0;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.*;

import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class TestYetAnotherMath {

    static final double PRECISION = 0.00001;

    @ParameterizedTest
    @ValueSource(doubles = { -1.1, 1.1 })
    public void testArctg_UndefinedX(double x) {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> YetAnotherMath.arctg(x));
        assertTrue(exception.getMessage().startsWith("arctg series converges only for |x| <= 1"));
    }

    @ParameterizedTest
    @ValueSource(doubles = { -1.0, 1.0 })
    public void testArctg_LimitExceeded(double x) {
        Exception exception = assertThrows(RuntimeException.class, () -> YetAnotherMath.arctg(x));
        assertEquals("ITERATION_LIMIT exceeded", exception.getMessage());
    }

    static final Supplier<Stream<Arguments>> testArctg_Correct = () ->
        Stream.of(
            arguments(-0.95, -0.759762),
            arguments(-0.5, -0.463647),
            arguments(0.0, 0.0),
            arguments(0.5, 0.463647),
            arguments(0.95, 0.759762)
        );

    @ParameterizedTest
    @FieldSource
    public void testArctg_Correct(double x, double expected) {
        assertEquals(expected, YetAnotherMath.arctg(x), PRECISION);
    }

    static Stream<Double> generateRandomValues() {
        Random random = new Random(69);
        return random.doubles(-0.95, 0.95).limit(100).boxed();
    }

    @ParameterizedTest
    @MethodSource("generateRandomValues")
    public void testArctg_SymmetryProperty(double x) {
        double result1 = YetAnotherMath.arctg(x);
        double result2 = YetAnotherMath.arctg(-x);
        assertEquals(
            -result2,
            result1,
            PRECISION,
            "Failed for x = " + x + ": arctg(" + x + ") = " + result1 + " but -arctg(" + (-x) + ") = " + (-result2)
        );
    }
}
