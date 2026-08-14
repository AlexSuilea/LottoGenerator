public class JokerGenerator {

    public static void main(String[] args) {

        var tickets =
                JokerOptimizer.generateOptimizedTickets(
                        4,
                        5_000
                );

        System.out.println("\nJoker:");

        tickets.forEach(System.out::println);
    }
}