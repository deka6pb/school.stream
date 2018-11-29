package school.demo1;

import java.util.stream.LongStream;

public class Leibniz {

    public static final long SIZE = 100_000_000;

    public static void main(String[] args) {
        Leibniz leibniz = new Leibniz();
        System.out.println("Get Pi by using Old School!");
        System.out.println(leibniz.getPiOldSchool());
        System.out.println("Get Pi by using Stream!");
        System.out.println(leibniz.getPiStream());
        System.out.println("Get Pi by using Parallel Stream!");
        System.out.println(leibniz.getPiParallelStream());
//        System.out.println("Get Pi by using Zip!");
//        System.out.println(carlo.getPiZipBoxed());
//        System.out.println("Get Pi by using DoubleStreamZip!");
//        System.out.println(carlo.getPiZipDouble());
    }

    public double getPiOldSchool() {
        double r = 0.0;
        for (long i = 0; i < SIZE; i++) {
            r += ((i & 1) == 0 ? 1.0 : -1.0) / (2 * i + 1);
        }


        return r * 4;
    }

    public double getPiStream() {
        return 4.0 * LongStream.range(0, SIZE)
                .mapToDouble(n -> ((n & 1) == 0 ? 1.0 : -1.0) / (2 * n + 1))
                .sum();
    }

    public double getPiParallelStream() {
        return 4.0 * LongStream.range(0, SIZE)
                .mapToDouble(n -> ((n & 1) == 0 ? 1.0 : -1.0) / (2 * n + 1))
                .parallel()
                .sum();
    }
}
