package me.foesio.core.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FoScheduler {
    private final JavaPlugin plugin;

    public FoScheduler() {
        this(null);
    }

    public FoScheduler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isFolia() {
        return false;
    }

    public void runForPlayer(Player player, Runnable task) {
        if (plugin != null && plugin.isEnabled()) {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public void runLaterForPlayer(Player player, Runnable task, long delayTicks) {
        if (plugin != null && plugin.isEnabled()) {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }
}
