package me.foesio.core.storage;

import me.foesio.core.scheduler.FoScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class WriteBehindStore<K, V> {
    private static final long MILLIS_PER_TICK = 50L;

    private final long deferredFlushDelayMillis;
    private final BiFunction<K, Boolean, V> snapshotFunction;
    private final BiFunction<K, V, Boolean> saveFunction;
    private final Set<K> dirtyKeys = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> pendingFlush;

    private WriteBehindStore(
        long deferredFlushDelayTicks,
        BiFunction<K, Boolean, V> snapshotFunction,
        BiFunction<K, V, Boolean> saveFunction
    ) {
        this.deferredFlushDelayMillis = Math.max(1L, deferredFlushDelayTicks) * MILLIS_PER_TICK;
        this.snapshotFunction = snapshotFunction;
        this.saveFunction = saveFunction;

        ThreadFactory threadFactory = runnable -> {
            Thread thread = new Thread(runnable, "FoOrders-WriteBehind");
            thread.setDaemon(true);
            return thread;
        };
        this.executor = Executors.newSingleThreadScheduledExecutor(threadFactory);
    }

    public static <K, V> WriteBehindStore<K, V> create(
        FoScheduler scheduler,
        long deferredFlushDelayTicks,
        BiFunction<K, Boolean, V> snapshotFunction,
        BiFunction<K, V, Boolean> saveFunction
    ) {
        return new WriteBehindStore<>(deferredFlushDelayTicks, snapshotFunction, saveFunction);
    }

    public void snapshotAndWriteAsync(K key, boolean unload) {
        if (key == null) {
            return;
        }
        dirtyKeys.remove(key);

        V snapshot = snapshot(key, unload);
        if (snapshot == null) {
            return;
        }
        submit(() -> save(key, snapshot));
    }

    public void flushSynchronously(List<K> keys, boolean unload) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        for (K key : keys) {
            if (key == null) {
                continue;
            }
            dirtyKeys.remove(key);
            V snapshot = snapshot(key, unload);
            if (snapshot != null) {
                save(key, snapshot);
            }
        }
    }

    public void writeAsync(K key, V value) {
        if (key == null || value == null) {
            return;
        }
        dirtyKeys.remove(key);
        submit(() -> save(key, value));
    }

    public void markDirty(K key) {
        if (key == null) {
            return;
        }
        dirtyKeys.add(key);
        scheduleDeferredFlush();
    }

    private synchronized void scheduleDeferredFlush() {
        if (pendingFlush != null && !pendingFlush.isDone()) {
            return;
        }
        try {
            pendingFlush = executor.schedule(this::flushDirtyKeys, deferredFlushDelayMillis, TimeUnit.MILLISECONDS);
        } catch (RuntimeException exception) {
            // Executor rejected the task (shutting down); fall back to writing on this thread
            // so a pending change is never silently dropped.
            flushDirtyKeys();
        }
    }

    private void flushDirtyKeys() {
        for (K key : new ArrayList<>(dirtyKeys)) {
            if (!dirtyKeys.remove(key)) {
                continue;
            }
            V snapshot = snapshot(key, false);
            if (snapshot != null) {
                save(key, snapshot);
            }
        }
    }

    private V snapshot(K key, boolean unload) {
        if (snapshotFunction == null) {
            return null;
        }
        return snapshotFunction.apply(key, unload);
    }

    private void save(K key, V value) {
        if (saveFunction != null) {
            saveFunction.apply(key, value);
        }
    }

    private void submit(Runnable task) {
        try {
            executor.execute(task);
        } catch (RuntimeException exception) {
            // Executor rejected the task (shutting down); write on this thread instead
            // so the change is not lost.
            task.run();
        }
    }
}
