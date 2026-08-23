package joker.optimized;

public class JokerGenerator {

    public static void main(String[] args) {

        var result =
                JokerFourMatchOptimizer.optimize(
                        2000,
                        5_000
                );

        result.print();
    }
}