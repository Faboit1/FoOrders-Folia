package me.foesio.core.storage;

import me.foesio.core.scheduler.FoScheduler;

import java.util.List;
import java.util.function.BiFunction;

public class WriteBehindStore<K, V> {
    private WriteBehindStore() {}

    public static <K, V> WriteBehindStore<K, V> create(
        FoScheduler scheduler,
        long deferredFlushDelayTicks,
        BiFunction<K, Boolean, V> snapshotFunction,
        BiFunction<K, V, Boolean> saveFunction
    ) {
        throw new UnsupportedOperationException("stub");
    }

    public void snapshotAndWriteAsync(K key, boolean unload) {
        throw new UnsupportedOperationException("stub");
    }

    public void flushSynchronously(List<K> keys, boolean unload) {
        throw new UnsupportedOperationException("stub");
    }

    public void writeAsync(K key, V value) {
        throw new UnsupportedOperationException("stub");
    }

    public void markDirty(K key) {
        throw new UnsupportedOperationException("stub");
    }
}
