package me.foesio.core.reload;

import org.bukkit.plugin.java.JavaPlugin;

public class FoReloadRegistry {
    private FoReloadRegistry() {}

    public static FoReloadRegistry create() {
        throw new UnsupportedOperationException("stub");
    }

    public FoReloadRegistry addConfig(JavaPlugin plugin) {
        throw new UnsupportedOperationException("stub");
    }

    public FoReloadRegistry add(String name, Runnable step) {
        throw new UnsupportedOperationException("stub");
    }

    public FoReloadResult reload() {
        throw new UnsupportedOperationException("stub");
    }
}
