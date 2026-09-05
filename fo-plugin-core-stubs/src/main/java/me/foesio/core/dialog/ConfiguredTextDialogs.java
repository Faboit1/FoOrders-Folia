package me.foesio.core.dialog;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Text dialogs defined in {@code dialogs/<id>.yml}, each falling back to a
 * built-in request when the file is missing or a field is unset.
 */
public class ConfiguredTextDialogs {
    private static final String FOLDER = "dialogs";

    private final Plugin plugin;
    private final Map<String, TextDialogRequest> fallbacks = new LinkedHashMap<>();
    private final Map<String, FileConfiguration> loaded = new ConcurrentHashMap<>();

    private ConfiguredTextDialogs(Plugin plugin) {
        this.plugin = plugin;
    }

    public static ConfiguredTextDialogs create(Plugin plugin) {
        return new ConfiguredTextDialogs(plugin);
    }

    public ConfiguredTextDialogs register(String id, TextDialogRequest fallback) {
        if (id != null && !id.isBlank()) {
            fallbacks.put(id, fallback);
        }
        return this;
    }

    public void reload() {
        loaded.clear();
        if (plugin == null) {
            return;
        }
        for (String id : fallbacks.keySet()) {
            File file = new File(plugin.getDataFolder(), FOLDER + File.separator + id + ".yml");
            if (!file.exists()) {
                try {
                    plugin.saveResource(FOLDER + "/" + id + ".yml", false);
                } catch (IllegalArgumentException exception) {
                    // No bundled default for this id; the fallback request is used.
                }
            }
            if (file.exists()) {
                loaded.put(id, YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    public TextDialogRequest request(String id, TextDialogRequest fallback, Map<String, String> placeholders) {
        TextDialogRequest base = fallback != null ? fallback : fallbacks.get(id);
        FileConfiguration config = id == null ? null : loaded.get(id);
        if (config == null || base == null) {
            return applyPlaceholders(base, placeholders);
        }

        return applyPlaceholders(new TextDialogRequest(
            config.getString("title", base.title()),
            body(config, base.body()),
            config.getString("field-label", base.fieldLabel()),
            config.getString("initial-value", base.initialValue()),
            config.getString("placeholder", base.placeholder()),
            button(config.getConfigurationSection("confirm-button"), base.submitButton()),
            button(config.getConfigurationSection("cancel-button"), base.cancelButton()),
            config.getInt("body-width", base.bodyWidth()),
            config.getInt("input-width", base.inputWidth()),
            config.getInt("max-length", base.maxLength()),
            config.getBoolean("label-visible", base.labelVisible()),
            config.getBoolean("can-close-with-escape", base.canCloseWithEscape()),
            config.getBoolean("pause", base.pause())
        ), placeholders);
    }

    /** Body accepts either a single string or a list of lines. */
    private List<String> body(FileConfiguration config, List<String> fallback) {
        if (config.isList("body")) {
            return List.copyOf(config.getStringList("body"));
        }
        String single = config.getString("body");
        if (single != null) {
            return List.of(single);
        }
        return fallback;
    }

    private DialogButton button(ConfigurationSection section, DialogButton fallback) {
        if (section == null) {
            return fallback;
        }
        return DialogButton.icon(
            section.getString("icon", fallback == null ? "" : fallback.icon()),
            section.getString("label", fallback == null ? "" : fallback.label()),
            section.getString("tooltip", fallback == null ? "" : fallback.tooltip()),
            section.getInt("width", fallback == null ? 0 : fallback.width())
        );
    }

    private TextDialogRequest applyPlaceholders(TextDialogRequest request, Map<String, String> placeholders) {
        if (request == null || placeholders == null || placeholders.isEmpty()) {
            return request;
        }

        List<String> body = new ArrayList<>();
        if (request.body() != null) {
            for (String line : request.body()) {
                body.add(replace(line, placeholders));
            }
        }

        return new TextDialogRequest(
            replace(request.title(), placeholders),
            List.copyOf(body),
            replace(request.fieldLabel(), placeholders),
            replace(request.initialValue(), placeholders),
            replace(request.placeholder(), placeholders),
            replaceButton(request.submitButton(), placeholders),
            replaceButton(request.cancelButton(), placeholders),
            request.bodyWidth(),
            request.inputWidth(),
            request.maxLength(),
            request.labelVisible(),
            request.canCloseWithEscape(),
            request.pause()
        );
    }

    private DialogButton replaceButton(DialogButton button, Map<String, String> placeholders) {
        if (button == null) {
            return null;
        }
        return DialogButton.icon(
            button.icon(),
            replace(button.label(), placeholders),
            replace(button.tooltip(), placeholders),
            button.width()
        );
    }

    private String replace(String text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String replaced = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            replaced = replaced.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
        }
        return replaced;
    }
}
