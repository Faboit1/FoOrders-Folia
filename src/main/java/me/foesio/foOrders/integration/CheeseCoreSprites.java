package me.foesio.foOrders.integration;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

/**
 * Reflective bridge to CheeseCore's sprite API.
 *
 * <p>CheeseCore is an optional runtime dependency (softdepend), and it is not
 * published to any Maven repository this project can build against, so it is
 * reached reflectively. Every method degrades to "no sprite" when CheeseCore is
 * absent, disabled, or a call fails, and the caller falls back to a text label.
 *
 * <p>The sprite for a material is version dependent - item textures moved from
 * the {@code minecraft:blocks} atlas to {@code minecraft:items} in 1.21.11 - so
 * lookups are resolved per viewer. CheeseCore detects the viewer's real client
 * version through ViaVersion where present.
 */
public final class CheeseCoreSprites {
    private static final String CHEESECORE_CLASS = "top.cheesesmp.cheesecore.api.CheeseCore";
    private static final String SPRITE_SERVICE_CLASS = "top.cheesesmp.cheesecore.api.SpriteService";
    private static final String CLIENT_VERSION_CLASS = "top.cheesesmp.cheesecore.api.ClientVersion";

    private static volatile Bridge bridge;
    private static volatile boolean resolved;

    private CheeseCoreSprites() {
    }

    /** Whether CheeseCore is present and its sprite service is enabled. */
    public static boolean isAvailable() {
        Bridge current = bridge();
        if (current == null) {
            return false;
        }
        try {
            return (boolean) current.isAvailable.invoke(null);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            return false;
        }
    }

    /**
     * A ready-to-render icon component for a material as this viewer's client
     * sees it, or {@code null} when no sprite is available.
     *
     * <p>CheeseCore already substitutes a text fallback for clients too old to
     * render sprite components, so the result is always safe to send.
     */
    public static Component icon(Material material, Player viewer) {
        if (material == null || viewer == null) {
            return null;
        }
        Bridge current = bridge();
        if (current == null) {
            return null;
        }
        try {
            if (!(boolean) current.isAvailable.invoke(null)) {
                return null;
            }
            Object service = current.sprites.invoke(null);
            Object icon = current.icon.invoke(service, material, viewer);
            return icon instanceof Component component ? component : null;
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            return null;
        }
    }

    /**
     * Whether this viewer's client can render sprite components at all.
     *
     * <p>Used to decide whether a dialog is worth building with sprite labels;
     * clients below 1.21.9 get plain text ones instead. Defaults to {@code true}
     * when CheeseCore cannot answer, since {@link #icon} falls back on its own.
     */
    public static boolean viewerSupportsSprites(Player viewer) {
        if (viewer == null) {
            return false;
        }
        Bridge current = bridge();
        if (current == null) {
            return false;
        }
        try {
            if (!(boolean) current.isAvailable.invoke(null)) {
                return false;
            }
            Object service = current.sprites.invoke(null);
            Object version = current.versionOf.invoke(service, viewer);
            return (boolean) current.supportsSprites.invoke(version);
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            return true;
        }
    }

    /**
     * A short identifier for the viewer's client version, used to keep dialogs
     * cached per client rather than shared across mismatched versions.
     */
    public static String viewerVersionId(Player viewer) {
        if (viewer == null) {
            return "";
        }
        Bridge current = bridge();
        if (current == null) {
            return "";
        }
        try {
            if (!(boolean) current.isAvailable.invoke(null)) {
                return "";
            }
            Object service = current.sprites.invoke(null);
            Object version = current.versionOf.invoke(service, viewer);
            Object id = current.versionId.invoke(version);
            return id == null ? "" : id.toString();
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            return "";
        }
    }

    private static Bridge bridge() {
        if (resolved) {
            return bridge;
        }
        synchronized (CheeseCoreSprites.class) {
            if (resolved) {
                return bridge;
            }
            bridge = resolveBridge();
            resolved = true;
            return bridge;
        }
    }

    private static Bridge resolveBridge() {
        try {
            Class<?> cheeseCore = Class.forName(CHEESECORE_CLASS);
            Class<?> spriteService = Class.forName(SPRITE_SERVICE_CLASS);
            Class<?> clientVersion = Class.forName(CLIENT_VERSION_CLASS);
            return new Bridge(
                cheeseCore.getMethod("isAvailable"),
                cheeseCore.getMethod("sprites"),
                spriteService.getMethod("icon", Material.class, Player.class),
                spriteService.getMethod("versionOf", Player.class),
                clientVersion.getMethod("supportsSprites"),
                clientVersion.getMethod("id")
            );
        } catch (ReflectiveOperationException | RuntimeException | LinkageError exception) {
            return null;
        }
    }

    private record Bridge(
        Method isAvailable,
        Method sprites,
        Method icon,
        Method versionOf,
        Method supportsSprites,
        Method versionId
    ) {
    }
}
