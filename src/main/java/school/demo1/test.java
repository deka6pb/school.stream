package school.demo1;

import java.math.BigInteger;

public class test {

    private static final long SIZE = 100;

    public static BigInteger seqSum() {
        BigInteger first = BigInteger.ZERO;
        BigInteger second = BigInteger.ONE;
        BigInteger sum = BigInteger.ZERO;
        for (int i = 0; i < SIZE; i++) {
            BigInteger s = second.add(first);
            first = second;
            second = s;
            sum = sum.add(first);
        }

        return sum;
    }

    public static void main( String[] args) throws Exception
    {
        org.openjdk.jmh.Main.main(args);

        System.out.println("generate = " + FibGenerate.stream().parallel().limit(SIZE).reduce(BigInteger.ZERO, (x, y) -> y.add(x)));
        System.out.println("iterate = " + FibIterate.stream().parallel().limit(SIZE).reduce(BigInteger.ZERO, (x, y) -> y.add(x)));
        System.out.println("iterator = " + FibIterator.stream().parallel().limit(SIZE).reduce(BigInteger.ZERO, (x, y) -> y.add(x)));
        System.out.println("oldSchool = " + seqSum());
    }
}
