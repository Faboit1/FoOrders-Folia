package me.foesio.core.dialog;

import org.bukkit.plugin.Plugin;

public class DialogService {
    private final NativeDialogSupport support;

    public DialogService() {
        this(null);
    }

    public DialogService(Plugin plugin) {
        NativeDialogSettings settings = NativeDialogSettings.fromConfig(
            plugin == null ? null : plugin.getConfig().getConfigurationSection("native-dialogs")
        );
        this.support = NativeDialogSupport.detect(plugin, settings);
    }

    public NativeDialogSupport support() {
        return support;
    }
}
