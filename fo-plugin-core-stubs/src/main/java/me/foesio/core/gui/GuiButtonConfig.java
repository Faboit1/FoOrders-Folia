package me.foesio.core.gui;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class GuiButtonConfig {
    private GuiButtonConfig() {}

    public static GuiButtonConfig defaults() {
        throw new UnsupportedOperationException("stub");
    }

    public static GuiButtonConfig fromGuiFile(YamlConfiguration yaml) {
        throw new UnsupportedOperationException("stub");
    }

    public ItemStack search(String searchText) {
        throw new UnsupportedOperationException("stub");
    }

    public ItemStack previousPage(int currentPage, int maxPage) {
        throw new UnsupportedOperationException("stub");
    }

    public ItemStack nextPage(int currentPage, int maxPage) {
        throw new UnsupportedOperationException("stub");
    }

    public ItemStack back() {
        throw new UnsupportedOperationException("stub");
    }
}
