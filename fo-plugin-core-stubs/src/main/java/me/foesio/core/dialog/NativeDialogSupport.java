package me.foesio.core.dialog;

import org.bukkit.plugin.Plugin;

public class NativeDialogSupport {
    private NativeDialogSupport() {}

    public static NativeDialogSupport detect(Plugin plugin, NativeDialogSettings settings) {
        throw new UnsupportedOperationException("stub");
    }

    public boolean configEnabled() {
        throw new UnsupportedOperationException("stub");
    }

    public boolean canUseNativeDialogs() {
        throw new UnsupportedOperationException("stub");
    }

    public boolean serverSupportsNativeDialogs() {
        throw new UnsupportedOperationException("stub");
    }

    public boolean runtimeDisabled() {
        throw new UnsupportedOperationException("stub");
    }

    public String runtimeUnavailableReason() {
        throw new UnsupportedOperationException("stub");
    }

    public String unavailableReason() {
        throw new UnsupportedOperationException("stub");
    }

    public boolean warnOnFallback() {
        throw new UnsupportedOperationException("stub");
    }

    public void disableForSession(String reason) {
        throw new UnsupportedOperationException("stub");
    }
}
