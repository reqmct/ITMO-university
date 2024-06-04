package info.kgeorgiy.ja.shibanov.crawler;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceWithLimit {

    private Queue<Runnable> waiters;
    private final ExecutorService threadPool;
    private final int limit;
    private int current;


    public ExecutorServiceWithLimit(ExecutorService threadPool, int limit) {
        this.threadPool = threadPool;
        this.limit = limit;
        this.waiters = new ArrayDeque<>();
    }

    private Runnable createTaskWrapper(Runnable task) {
        return () -> {
            try {
                task.run();
            } finally {
                next();
            }
        };
    }

    public synchronized void submit(Runnable initialTask) {
        Runnable task = createTaskWrapper(initialTask);
        if (current < limit) {
            threadPool.submit(task);
            current++;
        } else {
            waiters.add(task);
        }
    }

    private synchronized void next() {
        Runnable task = waiters.poll();
        if (task == null) {
            current--;
        } else {
            threadPool.submit(task);
        }
    }

    public synchronized void clear() {
        if (waiters.isEmpty()) {
            waiters = new ArrayDeque<>();
        }
    }
}

