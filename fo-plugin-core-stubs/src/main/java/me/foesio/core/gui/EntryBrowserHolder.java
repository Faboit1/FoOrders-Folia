package me.foesio.core.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EntryBrowserHolder implements InventoryHolder {
    public EntryBrowserRequest request() {
        throw new UnsupportedOperationException("stub");
    }

    public int maxPage() {
        throw new UnsupportedOperationException("stub");
    }

    @Override
    public Inventory getInventory() {
        throw new UnsupportedOperationException("stub");
    }
}
