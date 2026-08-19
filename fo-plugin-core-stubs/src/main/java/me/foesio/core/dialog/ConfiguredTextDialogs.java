package me.foesio.core.dialog;

import org.bukkit.plugin.Plugin;

import java.util.Map;

public class ConfiguredTextDialogs {
    private ConfiguredTextDialogs() {}

    public static ConfiguredTextDialogs create(Plugin plugin) {
        throw new UnsupportedOperationException("stub");
    }

    public ConfiguredTextDialogs register(String id, TextDialogRequest fallback) {
        throw new UnsupportedOperationException("stub");
    }

    public void reload() {
        throw new UnsupportedOperationException("stub");
    }

    public TextDialogRequest request(String id, TextDialogRequest fallback, Map<String, String> placeholders) {
        throw new UnsupportedOperationException("stub");
    }
}
