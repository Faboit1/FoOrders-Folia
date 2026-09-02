package me.foesio.core;

public class DialogSpriteSupport {
    private static final String CHEESECORE_API = "top.cheesesmp.cheesecore.api.CheeseCore";

    private final boolean available;

    public DialogSpriteSupport() {
        this.available = detect();
    }

    private static boolean detect() {
        try {
            Class.forName(CHEESECORE_API);
            return true;
        } catch (ClassNotFoundException | LinkageError exception) {
            return false;
        }
    }

    public boolean available() {
        return available;
    }

    public String unavailableReason() {
        return available ? null : "CheeseCore is not installed, so item buttons show text labels instead of sprites";
    }
}
