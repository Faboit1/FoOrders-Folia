package me.foesio.foOrders.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.Map;

/**
 * The sounds played for menu actions.
 *
 * <p>Sounds are addressed by their vanilla name rather than the {@code Sound}
 * enum, because that enum's shape changed across recent Minecraft versions;
 * passing the name through means a server can also use a resource pack sound.
 * An unset or blank name silences that action.
 */
public final class GuiSounds {
    /** Sound keys, each mapped to its vanilla default. */
    private static final Map<String, String> DEFAULTS = Map.of(
        "click", "ui.button.click",
        "page-turn", "item.book.page_turn",
        "open", "block.chest.open",
        "success", "entity.experience_orb.pickup",
        "error", "block.note_block.bass"
    );
    private static final float DEFAULT_VOLUME = 0.6F;
    private static final float DEFAULT_PITCH = 1.0F;

    private final Plugin plugin;
    private volatile boolean enabled = true;
    private volatile Map<String, Sound> sounds = Map.of();

    public GuiSounds(Plugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("sounds");
        enabled = section == null || section.getBoolean("enabled", true);

        Map<String, Sound> loaded = new java.util.LinkedHashMap<>();
        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            String key = entry.getKey();
            ConfigurationSection soundSection = section == null ? null : section.getConfigurationSection(key);

            String name;
            float volume = DEFAULT_VOLUME;
            float pitch = DEFAULT_PITCH;
            if (soundSection != null) {
                name = soundSection.getString("sound", entry.getValue());
                volume = (float) soundSection.getDouble("volume", DEFAULT_VOLUME);
                pitch = (float) soundSection.getDouble("pitch", DEFAULT_PITCH);
            } else {
                // Also accept the short form "click: ui.button.click".
                name = section == null ? entry.getValue() : section.getString(key, entry.getValue());
            }

            if (name != null && !name.isBlank()) {
                loaded.put(key, new Sound(normalize(name), clamp(volume), clamp(pitch)));
            }
        }
        sounds = Map.copyOf(loaded);
    }

    public void click(Player player) {
        play(player, "click");
    }

    public void pageTurn(Player player) {
        play(player, "page-turn");
    }

    public void open(Player player) {
        play(player, "open");
    }

    public void success(Player player) {
        play(player, "success");
    }

    public void error(Player player) {
        play(player, "error");
    }

    /**
     * Plays one sound to a single player.
     *
     * <p>Callers are already on the player's region thread, which is what Folia
     * requires; a failure here must never interrupt the menu action that
     * triggered it, so any error is swallowed.
     */
    private void play(Player player, String key) {
        if (!enabled || player == null || !player.isOnline()) {
            return;
        }
        Sound sound = sounds.get(key);
        if (sound == null) {
            return;
        }
        try {
            player.playSound(player.getLocation(), sound.name(), sound.volume(), sound.pitch());
        } catch (RuntimeException exception) {
            // An unknown sound name should not break the menu.
        }
    }

    private String normalize(String name) {
        String trimmed = name.trim().toLowerCase(Locale.ROOT);
        return trimmed.startsWith("minecraft:") ? trimmed.substring("minecraft:".length()) : trimmed;
    }

    private float clamp(float value) {
        if (!Float.isFinite(value) || value < 0F) {
            return 0F;
        }
        return Math.min(value, 2F);
    }

    private record Sound(String name, float volume, float pitch) {
    }
}
