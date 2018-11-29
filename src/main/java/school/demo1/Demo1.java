package school.demo1;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

public class Demo1 {

    public static final int SIZE = 4096;


    static BinaryOperator<BigInteger> action = (x, y) -> x.add(y);

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


    public static void main(String[] args) throws Exception {
//        org.openjdk.jmh.Main.main(args);

        System.out.println("Fibonachi Generate");
        System.out.println(FibGenerate.parSum());
        System.out.println("Fibonachi Iterate");
        System.out.println(FibIterate.parSum());
        System.out.println("Fibonachi Iterator");
        System.out.println(FibIterator.parSum());
        System.out.println("Fibonachi old school");
        System.out.println(seqSum());
    }
}
