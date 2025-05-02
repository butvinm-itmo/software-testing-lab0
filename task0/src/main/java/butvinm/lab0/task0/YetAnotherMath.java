package butvinm.lab0.task0;

public class YetAnotherMath {
    private static final double EPS = 0.00001;

    private static final int ITERATION_LIMIT = 10000;

    public static double arctg(double x) throws IllegalArgumentException, RuntimeException {
        if (Math.abs(x) > 1) {
            throw new IllegalArgumentException("arctg series converges only for |x| <= 1, but provided x is '%f'".formatted(x));
        }

        double result = 0.0;
        double prevResult = Double.POSITIVE_INFINITY;

        int n = 0;
        while (Math.abs(result - prevResult) > EPS) {
            prevResult = result;

            int exponent = 2 * n + 1;
            double term = Math.pow(x, exponent) / exponent;
            result += Math.pow(-1, n) * term;
            n += 1;
            if (n > ITERATION_LIMIT) {
                throw new RuntimeException("ITERATION_LIMIT exceeded");
            }
        }

        return result;
    }
}
