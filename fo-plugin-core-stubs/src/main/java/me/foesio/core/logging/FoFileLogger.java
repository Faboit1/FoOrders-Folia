package me.foesio.core.logging;

import org.bukkit.plugin.Plugin;

public class FoFileLogger {
    private FoFileLogger() {}

    public static FoFileLogger create(Plugin plugin) {
        throw new UnsupportedOperationException("stub");
    }

    public void configureFromConfig(String path, boolean defaultValue) {
        throw new UnsupportedOperationException("stub");
    }

    public void info(String message) {
        throw new UnsupportedOperationException("stub");
    }

    public void warn(String message) {
        throw new UnsupportedOperationException("stub");
    }

    public void error(String message, Throwable throwable) {
        throw new UnsupportedOperationException("stub");
    }

    public void close() {
        throw new UnsupportedOperationException("stub");
    }
}
