package school.demo1;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Zipper {


    public static <A, B, C> Stream<C> zip(Stream<? super A> a, Stream<? super B> b, BiFunction<? super A, ? super B, ? extends C> zipper) {
        Objects.requireNonNull(zipper);
        Spliterator<A> as = (Spliterator<A>) Objects.requireNonNull(a).spliterator();
        Spliterator<B> bs = (Spliterator<B>) Objects.requireNonNull(b).spliterator();

        int characteristics = as.characteristics() & bs.characteristics() & ~(Spliterator.DISTINCT | Spliterator.SORTED);
        long size = Math.min(as.estimateSize(), bs.estimateSize());

        Spliterator<C> cs = new ZipperSpliterator<>(as, bs, zipper, size, characteristics);
        return (a.isParallel() || b.isParallel()) ? StreamSupport.stream(cs, true) : StreamSupport.stream(cs, false);
    }

    public static DoubleStream zip(DoubleStream a, DoubleStream b, DoubleBinaryOperator zipper) {
        Objects.requireNonNull(zipper);
        Spliterator.OfDouble as = Objects.requireNonNull(a).spliterator();
        Spliterator.OfDouble bs = Objects.requireNonNull(b).spliterator();

        int characteristics = as.characteristics() & bs.characteristics() & ~(Spliterator.DISTINCT | Spliterator.SORTED);
        long size = Math.min(as.estimateSize(), bs.estimateSize());

        Spliterator.OfDouble cs = new DoubleZipperSpliterator(as, bs, zipper, size, characteristics);
        return (a.isParallel() || b.isParallel()) ? StreamSupport.doubleStream(cs, true) : StreamSupport.doubleStream(cs, false);
    }

    private static final class ExtractingConsumer<T> implements Consumer<T> {
        private T value;

        @Override
        public void accept(T t) {
            this.value = t;
        }

        public T get() {
            return value;
        }
    }

    private static class ZipperSpliterator<A, B, C> extends Spliterators.AbstractSpliterator<C> {

        final Spliterator<A> as;
        final Spliterator<B> bs;
        final BiFunction<? super A, ? super B, ? extends C> zipper;
        final ExtractingConsumer<A> aExtractingConsumer;
        final ExtractingConsumer<B> bExtracting;


        ZipperSpliterator(Spliterator<A> as, Spliterator<B> bs, BiFunction<? super A, ? super B, ? extends C> zipper, long size, int additionalCharacteristics) {
            super(size, additionalCharacteristics);
            this.as = as;
            this.bs = bs;
            this.zipper = zipper;
            this.aExtractingConsumer = new ExtractingConsumer<>();
            this.bExtracting = new ExtractingConsumer<>();
        }

        /**
         * Creates a spliterator reporting the given estimated size and
         * additionalCharacteristics.
         */
//        protected ZipperListarator() {
////            super(Stream as, Stream bs, est, additionalCharacteristics);
//        }
        @Override
        public boolean tryAdvance(Consumer<? super C> action) {
            if (as.tryAdvance(aExtractingConsumer) && bs.tryAdvance(bExtracting)) {

                //action.accept(zipper.apply(aExtractingConsumer.get(),bExtractor.get()));
                return true;
            }
            return false;
        }
    }

    private static class DoubleZipperSpliterator implements Spliterator.OfDouble {

        final Spliterator.OfDouble as;
        final Spliterator.OfDouble bs;
        final DoubleBinaryOperator zipper;
        final DoubleExtractingConsumer aExtractingConsumer;
        final DoubleExtractingConsumer bExtracting;


        DoubleZipperSpliterator(Spliterator.OfDouble as, Spliterator.OfDouble bs, DoubleBinaryOperator zipper, long size, int additionalCharacteristics) {
            super();
            this.as = as;
            this.bs = bs;
            this.zipper = zipper;
            this.aExtractingConsumer = new DoubleExtractingConsumer();
            this.bExtracting = new DoubleExtractingConsumer();
        }

        /**
         * Creates a spliterator reporting the given estimated size and
         * additionalCharacteristics.
         */
//        protected ZipperListarator() {
////            super(Stream as, Stream bs, est, additionalCharacteristics);
//        }
        @Override
        public boolean tryAdvance(DoubleConsumer action) {
            if (as.tryAdvance(aExtractingConsumer) && bs.tryAdvance(bExtracting)) {
                action.accept((Double) zipper.applyAsDouble(aExtractingConsumer.get(), bExtracting.get()));
                return true;
            }
            return false;
        }

        @Override
        public OfDouble trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }


    }

    public static DoubleStream unsafeZip(DoubleStream a,
                                         DoubleStream b,
                                         DoubleBinaryOperator zipper) {
        Objects.requireNonNull(zipper);

        Spliterator.OfDouble as = Objects.requireNonNull(a).spliterator();
        Spliterator.OfDouble bs = Objects.requireNonNull(b).spliterator();

        int characteristics = as.characteristics() & bs.characteristics() & ~(Spliterator.DISTINCT | Spliterator.SORTED);
        long size = Math.min(as.estimateSize(), bs.estimateSize());

        Spliterator.OfDouble cs = new UnsafeDoubleZipperSpliterator(as, bs, zipper, size, characteristics);
        return (a.isParallel() || b.isParallel())
                ? StreamSupport.doubleStream(cs, true)
                : StreamSupport.doubleStream(cs, false);
    }

    private static final class UnsafeDoubleZipperSpliterator extends Spliterators.AbstractDoubleSpliterator {
        final Spliterator.OfDouble as;
        final Spliterator.OfDouble bs;
        final DoubleBinaryOperator zipper;
        final DoubleExtractingConsumer aExtractor;
        final DoubleExtractingConsumer bExtractor;


        UnsafeDoubleZipperSpliterator(Spliterator.OfDouble as,
                                      Spliterator.OfDouble bs,
                                      DoubleBinaryOperator zipper,
                                      long size,
                                      int additionalCharacteristics) {
            super(size, additionalCharacteristics);
            this.as = as;
            this.bs = bs;
            this.zipper = zipper;
            this.aExtractor = new DoubleExtractingConsumer();
            this.bExtractor = new DoubleExtractingConsumer();
        }

        @Override
        public OfDouble trySplit() {
            Spliterator.OfDouble aSplitted = as.trySplit();
            Spliterator.OfDouble bSplitted = bs.trySplit();
            if (aSplitted != null && bSplitted != null) {
                return new UnsafeDoubleZipperSpliterator(aSplitted, bSplitted, zipper, estimateSize(), characteristics());
            } else {
                assert aSplitted == null && bSplitted == null;
                return null;
            }
        }

        @Override
        public boolean tryAdvance(DoubleConsumer action) {
            if (as.tryAdvance(aExtractor) && bs.tryAdvance(bExtractor)) {
                action.accept((Double) zipper.applyAsDouble(aExtractor.get(), bExtractor.get()));
                return true;
            }
            return false;
        }

        @Override
        public long estimateSize() {
            return 0;
        }

        @Override
        public int characteristics() {
            return 0;
        }

    }

    private static class DoubleExtractingConsumer implements DoubleConsumer {
        private double value;

        @Override
        public void accept(double t) {
            this.value = t;
        }

        public double get() {
            return value;
        }

    }
}
