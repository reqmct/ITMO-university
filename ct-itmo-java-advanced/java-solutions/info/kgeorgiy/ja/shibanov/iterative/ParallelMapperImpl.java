package info.kgeorgiy.ja.shibanov.iterative;

import info.kgeorgiy.java.advanced.mapper.ParallelMapper;

import java.util.*;
import java.util.function.Function;

public class ParallelMapperImpl implements ParallelMapper {
    private final List<Thread> threads;
    private final SynchronizedQueueWrapper<Runnable> taskQueue = new SynchronizedQueueWrapper<>();

    private static class WaitGroup {
        private final int capacity;
        private int size;

        public WaitGroup(int capacity) {
            this.capacity = capacity;
        }

        public synchronized void ready() {
            size++;
            if (size == capacity) {
                notify();
            }
        }

        public synchronized void waitForCapacity() throws InterruptedException {
            while (size < capacity) {
                wait();
            }
        }
    }

    private static class SynchronizedQueueWrapper<E> {
        private final Queue<E> queue;

        public SynchronizedQueueWrapper() {
            queue = new ArrayDeque<>();
        }

        public synchronized void add(E task) {
            queue.add(task);
            notify();
        }

        public synchronized E poll() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            E task = queue.poll();
            notify();
            return task;
        }

    }

    public ParallelMapperImpl(int threads) {
        this.threads = new ArrayList<>();
        final Runnable threadLogic = () -> {
            while (!Thread.interrupted()) {
                try {
                    Runnable task = taskQueue.poll();
                    task.run();
                } catch (InterruptedException ignored) {
                    return;
                }
            }
        };
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(threadLogic);
            thread.start();
            this.threads.add(thread);
        }
    }


    @Override
    public <T, R> List<R> map(Function<? super T, ? extends R> f, List<? extends T> args) throws InterruptedException {
        List<R> result = new ArrayList<>(Collections.nCopies(args.size(), null));
        WaitGroup waitGroup = new WaitGroup(args.size());
        for (int i = 0; i < args.size(); i++) {
            int iCopy = i;
            taskQueue.add(
                    () -> {
                        try {
                            result.set(iCopy, f.apply(args.get(iCopy)));
                        } catch (RuntimeException e) {
                            System.err.println("Unexpected exception: " + e);
                        } finally {
                            waitGroup.ready();
                        }
                    }
            );
        }

        waitGroup.waitForCapacity();
        return result;
    }

    @Override
    public void close() {
        for (var thread : threads) {
            thread.interrupt();
        }

        boolean interrupted = false;

        for (var thread : threads) {
            while (true) {
                try {
                    thread.join();
                    break;
                } catch (InterruptedException ignored) {
                    interrupted = true;
                }
            }
        }

        if (interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
