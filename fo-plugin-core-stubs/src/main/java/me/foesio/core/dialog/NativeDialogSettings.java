package me.foesio.core.dialog;

import org.bukkit.configuration.ConfigurationSection;

public class NativeDialogSettings {
    private final boolean enabled;
    private final boolean itemSelection;
    private final boolean warnOnFallback;

    NativeDialogSettings(boolean enabled, boolean itemSelection, boolean warnOnFallback) {
        this.enabled = enabled;
        this.itemSelection = itemSelection;
        this.warnOnFallback = warnOnFallback;
    }

    public static NativeDialogSettings fromConfig(ConfigurationSection section) {
        if (section == null) {
            return new NativeDialogSettings(true, true, true);
        }
        return new NativeDialogSettings(
            section.getBoolean("enabled", true),
            section.getBoolean("item-selection", true),
            section.getBoolean("warn-on-fallback", true)
        );
    }

    public boolean enabled() {
        return enabled;
    }

    public boolean itemSelection() {
        return itemSelection;
    }

    public boolean warnOnFallback() {
        return warnOnFallback;
    }
}
