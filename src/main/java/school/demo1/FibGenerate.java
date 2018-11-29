package school.demo1;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
public class FibGenerate {
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

    @Benchmark
    public static BigInteger seqSum() {
        return stream().limit(SIZE).reduce(BigInteger.ZERO, BigInteger::add);
    }

    @Benchmark
    public static BigInteger parSum() {
        return stream().parallel().limit(SIZE).reduce(BigInteger.ZERO, BigInteger::add);
    }

    public static Stream<BigInteger> stream() {
        return Stream.generate(
                new Supplier<BigInteger>() {
                    private BigInteger first = BigInteger.ZERO;
                    private BigInteger second = BigInteger.ONE;

                    @Override
                    public BigInteger get() {
                        BigInteger s = second.add(first);
                        first = second;
                        second = s;
                        return first;
                    }
                }
        );

    }
}
