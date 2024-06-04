package info.kgeorgiy.ja.shibanov.iterative;

import info.kgeorgiy.java.advanced.iterative.NewListIP;
import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Implements parallel algorithms for working with lists.
 */
public class IterativeParallelism implements NewListIP {
    private final ParallelMapper parallelMapper;

    public IterativeParallelism() {
        this.parallelMapper = null;
    }

    public IterativeParallelism(ParallelMapper parallelMapper) {
        this.parallelMapper = parallelMapper;
    }

    private <T> List<Stream<? extends T>> splitValues(int threads, List<? extends T> values, int step) {
        ListWithStep<? extends T> valuesWithStep = new ListWithStep<>(values, step);
        List<Stream<? extends T>> streams = new ArrayList<>();
        int streamNumber = Math.min(valuesWithStep.size(), threads);
        int blockSize = valuesWithStep.size() / streamNumber;
        int rest = valuesWithStep.size() % streamNumber;
        int left = 0;
        for (int i = 0; i < streamNumber; i++) {
            int right = left + blockSize;
            if (rest != 0) {
                rest--;
                right++;
            }
            streams.add(valuesWithStep.subList(left, right).stream());
            left = right;
        }
        return streams;
    }

    private <T, M> List<M> doParallel(List<Stream<? extends T>> streamBlocks,
                                      Function<Stream<? extends T>, ? extends M> threadTask) throws InterruptedException {

        List<M> threadResults = new ArrayList<>(Collections.nCopies(streamBlocks.size(), null));
        List<Thread> activeThreads = new ArrayList<>();
        for (int i = 0; i < streamBlocks.size(); i++) {
            int iCopy = i;
            Thread thread = new Thread(
                    () -> threadResults.set(iCopy, threadTask.apply(streamBlocks.get(iCopy)))
            );
            activeThreads.add(thread);
            thread.start();
        }
        InterruptedException interruptedException = null;
        for (var thread : activeThreads) {
            while(true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException e) {
                    if (interruptedException == null) {
                        interruptedException = e;
                    } else {
                        interruptedException.addSuppressed(e);
                    }
                }
            }
        }
        if (interruptedException != null) {
            throw interruptedException;
        }
        return threadResults;
    }

    private <T, M, R> R doTask(int threads,
                               List<? extends T> values,
                               Function<Stream<? extends T>, ? extends M> threadTask,
                               Function<Stream<? extends M>, ? extends R> collectorFunction,
                               int step) throws InterruptedException {
        List<Stream<? extends T>> streamBlocks = splitValues(threads, values, step);
        if (streamBlocks.size() == 1) {
            return collectorFunction.apply(Stream.of(threadTask.apply(streamBlocks.getFirst())));
        }
        List<M> result;
        if (parallelMapper == null) {
            result = doParallel(streamBlocks, threadTask);
        } else {
            result = parallelMapper.map(threadTask, streamBlocks);
        }
        return collectorFunction.apply(result.stream());
    }

    /**
     * Joins the {@link String} representations of elements in parallel.
     *
     * @param threads the number of threads
     * @param values  the {@link List} of values to join
     * @param step    the step size
     * @return the concatenated {@link String}
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public String join(int threads, List<?> values, int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> stream.map(Object::toString).collect(Collectors.joining()),
                stream -> stream.collect(Collectors.joining()),
                step
        );
    }

    private <T, U> List<U> taskWithCollectToList(int threads,
                                                 List<? extends T> values,
                                                 Function<Stream<? extends T>, Stream<? extends U>> streamFunction,
                                                 int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> streamFunction.apply(stream).collect(Collectors.toList()),
                stream -> stream.flatMap(List::stream).collect(Collectors.toList()),
                step
        );
    }

    /**
     * Filters elements in parallel based on the specified {@link Predicate}.
     *
     * @param <T>       the type of elements in the {@link List}
     * @param threads   the number of threads
     * @param values    the {@link List} of values to filter
     * @param predicate the {@link Predicate} to apply for filtering
     * @param step      the step size
     * @return a {@link List} containing the filtered elements
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T> List<T> filter(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return taskWithCollectToList(threads,
                values,
                stream -> stream.filter(predicate),
                step
        );
    }

    /**
     * Transforms elements in parallel using the specified {@link Function}.
     *
     * @param <T>     the type of elements in the input {@link List}
     * @param <U>     the type of elements in the output {@link List}
     * @param threads the number of threads
     * @param values  the {@link List} of values to transform
     * @param f       the {@link Function} to apply for transformation
     * @param step    the step size
     * @return a {@link List} containing the transformed elements
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T, U> List<U> map(int threads, List<? extends T> values, Function<? super T, ? extends U> f, int step) throws InterruptedException {
        return taskWithCollectToList(threads,
                values,
                stream -> stream.map(f),
                step
        );
    }

    /**
     * Finds the maximum element in parallel based on the specified {@link Comparator}.
     *
     * @param <T>        the type of elements in the {@link List}
     * @param threads    the number of threads
     * @param values     the {@link List} of values to search for the maximum
     * @param comparator the {@link Comparator} to determine the order of elements
     * @param step       the step size
     * @return the maximum element according to the {@link Comparator}
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> stream.max(comparator).orElseThrow(),
                s -> s.max(comparator).orElseThrow(),
                step
        );
    }

    /**
     * Finds the minimum element in parallel based on the specified {@link Comparator}.
     *
     * @param <T>        the type of elements in the {@link List}
     * @param threads    the number of threads
     * @param values     the {@link List} of values to search for the minimum
     * @param comparator the {@link Comparator} to determine the order of elements
     * @param step       the step size
     * @return the minimum element according to the {@link Comparator}
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator, int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> stream.min(comparator).orElseThrow(),
                stream -> stream.min(comparator).orElseThrow(),
                step
        );
    }

    /**
     * Checks if all elements in the {@link List} match the given {@link Predicate} in parallel.
     *
     * @param <T>       the type of elements in the {@link List}
     * @param threads   the number of threads
     * @param values    the {@link List} of values to check
     * @param predicate the {@link Predicate} to apply for matching
     * @param step      the step size
     * @return {@code true} if all elements match the {@link Predicate}, {@code false} otherwise
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> stream.allMatch(predicate),
                stream -> stream.allMatch(Boolean::booleanValue),
                step
        );
    }

    /**
     * Checks if any elements in the {@link List} match the given {@link Predicate} in parallel.
     *
     * @param <T>       the type of elements in the {@link List}
     * @param threads   the number of threads
     * @param values    the {@link List} of values to check
     * @param predicate the {@link Predicate} to apply for matching
     * @param step      the step size
     * @return {@code true} if any element matches the {@link Predicate}, {@code false} otherwise
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> stream.anyMatch(predicate),
                stream -> stream.anyMatch(Boolean::booleanValue),
                step
        );
    }

    /**
     * Counts the number of elements in the {@link List} that match the given {@link Predicate} in parallel.
     *
     * @param <T>       the type of elements in the {@link List}
     * @param threads   the number of threads
     * @param values    the {@link List} of values to count
     * @param predicate the {@link Predicate} to apply for counting
     * @param step      the step size
     * @return the number of elements that match the {@link Predicate}
     * @throws InterruptedException if any thread is interrupted while executing the task
     */
    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate, int step) throws InterruptedException {
        return doTask(threads,
                values,
                stream -> stream.filter(predicate).count(),
                stream -> stream.mapToInt(Long::intValue).sum(),
                step
        );
    }

    private static class ListWithStep<E> extends AbstractList<E> {

        private final List<E> source;
        private final int step;
        private int size = -1;

        public ListWithStep(List<E> source, int step) {
            this.source = source;
            this.step = step;
        }

        @Override
        public E get(int index) {
            return source.get(index * step);
        }

        @Override
        public int size() {
            if (size == -1) {
                if (source.size() % step == 0) {
                    size = source.size() / step;
                } else {
                    size = source.size() / step + 1;
                }
            }
            return size;
        }
    }
}