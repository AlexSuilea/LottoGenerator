public class JokerGenerator {

    public static void main(String[] args) {

        var result =
                JokerFourMatchOptimizer.optimize(
                        4,
                        5_000
                );

        result.print();
    }
}