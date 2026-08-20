package me.foesio.core.inventory;

import org.bukkit.entity.Player;

public class InventoryCloseSuppressor {
    public void suppressNextClose(Player player) {}

    public boolean consumeSuppressedClose(Player player) {
        return false;
    }

    public void clear(Player player) {}
}
