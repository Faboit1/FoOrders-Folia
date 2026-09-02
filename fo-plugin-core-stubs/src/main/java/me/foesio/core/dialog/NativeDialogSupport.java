package me.foesio.core.dialog;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.atomic.AtomicReference;

public class NativeDialogSupport {
    private static final String DIALOG_CLASS = "io.papermc.paper.dialog.Dialog";

    private final Plugin plugin;
    private final NativeDialogSettings settings;
    private final boolean serverSupported;
    private final AtomicReference<String> sessionDisabledReason = new AtomicReference<>();

    NativeDialogSupport(Plugin plugin, NativeDialogSettings settings) {
        this.plugin = plugin;
        this.settings = settings == null ? NativeDialogSettings.fromConfig(null) : settings;
        this.serverSupported = detectServerSupport();
    }

    public static NativeDialogSupport detect(Plugin plugin, NativeDialogSettings settings) {
        return new NativeDialogSupport(plugin, settings);
    }

    private static boolean detectServerSupport() {
        try {
            Class.forName(DIALOG_CLASS);
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }

    public boolean configEnabled() {
        return settings.enabled();
    }

    public boolean itemSelectionEnabled() {
        return settings.enabled() && settings.itemSelection();
    }

    public boolean canUseNativeDialogs() {
        return serverSupported && settings.enabled() && !runtimeDisabled();
    }

    public boolean serverSupportsNativeDialogs() {
        return serverSupported;
    }

    public boolean runtimeDisabled() {
        return sessionDisabledReason.get() != null;
    }

    public String runtimeUnavailableReason() {
        return sessionDisabledReason.get();
    }

    public String unavailableReason() {
        String runtimeReason = sessionDisabledReason.get();
        if (runtimeReason != null) {
            return runtimeReason;
        }
        if (!serverSupported) {
            return "this server does not provide the Paper dialog API";
        }
        if (!settings.enabled()) {
            return "native dialogs are disabled in config.yml";
        }
        return null;
    }

    public boolean warnOnFallback() {
        return settings.warnOnFallback();
    }

    public void disableForSession(String reason) {
        String message = reason == null || reason.isBlank() ? "native dialogs failed at runtime" : reason;
        if (sessionDisabledReason.compareAndSet(null, message) && plugin != null) {
            plugin.getLogger().warning("Native dialogs disabled for this session: " + message);
        }
    }
}
