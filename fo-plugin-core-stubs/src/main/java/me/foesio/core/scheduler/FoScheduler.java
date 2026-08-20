package me.foesio.core.scheduler;

import org.bukkit.entity.Player;

public class FoScheduler {
    public boolean isFolia() {
        return false;
    }

    public void runForPlayer(Player player, Runnable task) {}

    public void runLaterForPlayer(Player player, Runnable task, long delayTicks) {}
}
