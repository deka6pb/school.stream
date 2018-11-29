package school.demo1;

import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.function.BinaryOperator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Euler2 {
    private static final BigInteger LIMIT = BigInteger.valueOf(4).multiply(BigInteger.TEN.pow(2048));

    static Predicate<BigInteger> filterPredicate = b -> !b.testBit(0);

    static Predicate<BigInteger> takeWhilePredicate = b -> b.compareTo(LIMIT) < 0;

    private static int SIZE = 4096;

    @Param({"1", "1024"}) private int backoff;

    private BinaryOperator<BigInteger> action;

    @Setup
    public void setup() {
        int back = backoff;
        action = (x, y) -> {
            Blackhole.consumeCPU(back);
            return x.add(y);
        };
    }

    public static BigInteger seqIterate() {
        Stream<BigInteger> s = FibIterate.stream().filter(filterPredicate);
        return TakeWhile.stream(s, takeWhilePredicate).reduce(BigInteger.ZERO, (x, y) -> y.add(x));
    }

    public static BigInteger parIterate() {
        Stream<BigInteger> s = FibIterate.stream().filter(filterPredicate);
        return TakeWhile.stream(s, takeWhilePredicate).parallel().reduce(BigInteger.ZERO, (x, y) -> y.add(x));
    }

    public static BigInteger seqIterator() {
        Stream<BigInteger> s = FibIterator.stream().filter(filterPredicate);
        return TakeWhile.stream(s, takeWhilePredicate).reduce(BigInteger.ZERO, (x, y) -> y.add(x));
    }

    public static BigInteger parIterator() {
        Stream<BigInteger> s = FibIterator.stream().filter(filterPredicate);
        return TakeWhile.stream(s, takeWhilePredicate).parallel().reduce(BigInteger.ZERO, (x, y) -> y.add(x));
    }

    public static BigInteger seqSum() {
        BigInteger first = BigInteger.ZERO;
        BigInteger second = BigInteger.ONE;
        BigInteger sum = BigInteger.ZERO;
        while (takeWhilePredicate.test(second)) {
            BigInteger s = second.add(first);
            first = second;
            second = s;
            if (filterPredicate.test(first)) {
                sum = sum.add(first);
            }
        }
        return sum;

    }

    public static void main(String[] args) {
        System.out.println("Sequential Iterate");
        System.out.println(seqIterate());
        System.out.println("Parallel Iterate");
        System.out.println(parIterate());
        System.out.println("Sequential Iterator");
        System.out.println(seqIterator());
        System.out.println("Parallel Iterator");
        System.out.println(parIterator());
        System.out.println("Sequential sum old school");
        System.out.println(seqSum());
    }
}
