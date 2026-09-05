package me.foesio.core.gui;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * The shared back, paging and search buttons, as defined by the {@code buttons}
 * section of {@code guis/orders.yml}.
 *
 * <p>Page numbers arrive zero based - every caller passes
 * {@code (currentPage - 1, pageCount - 1)} - and are rendered one based, so
 * {@code {page}} and {@code {max_page}} read the way a player expects.
 */
public class GuiButtonConfig {
    private static final Button DEFAULT_BACK = new Button(
        "IRON_DOOR",
        "§bʙᴀᴄᴋ",
        List.of("§fClick to return")
    );
    private static final Button DEFAULT_PREVIOUS_PAGE = new Button(
        "ARROW",
        "§bᴘʀᴇᴠɪᴏᴜꜱ ᴘᴀɢᴇ",
        List.of("§fPage {page}/{max_page}")
    );
    private static final Button DEFAULT_NEXT_PAGE = new Button(
        "ARROW",
        "§bɴᴇxᴛ ᴘᴀɢᴇ",
        List.of("§fPage {page}/{max_page}")
    );
    private static final Button DEFAULT_SEARCH = new Button(
        "NAME_TAG",
        "§bꜱᴇᴀʀᴄʜ",
        List.of("§fCurrent: {current}", "§fClick to search")
    );

    private final Button back;
    private final Button previousPage;
    private final Button nextPage;
    private final Button search;

    GuiButtonConfig(Button back, Button previousPage, Button nextPage, Button search) {
        this.back = back;
        this.previousPage = previousPage;
        this.nextPage = nextPage;
        this.search = search;
    }

    public static GuiButtonConfig defaults() {
        return new GuiButtonConfig(DEFAULT_BACK, DEFAULT_PREVIOUS_PAGE, DEFAULT_NEXT_PAGE, DEFAULT_SEARCH);
    }

    public static GuiButtonConfig fromGuiFile(YamlConfiguration yaml) {
        if (yaml == null) {
            return defaults();
        }
        return new GuiButtonConfig(
            read(yaml, "back", DEFAULT_BACK),
            read(yaml, "previous-page", DEFAULT_PREVIOUS_PAGE),
            read(yaml, "next-page", DEFAULT_NEXT_PAGE),
            read(yaml, "search", DEFAULT_SEARCH)
        );
    }

    private static Button read(YamlConfiguration yaml, String id, Button fallback) {
        String material = yaml.getString(id + ".material", fallback.material());
        String name = yaml.getString(id + ".name", fallback.name());
        List<String> lore = yaml.isList(id + ".lore")
            ? List.copyOf(yaml.getStringList(id + ".lore"))
            : fallback.lore();
        return new Button(material, name, lore);
    }

    public ItemStack search(String searchText) {
        String current = searchText == null || searchText.isBlank() ? "None" : searchText;
        return build(search, Map.of("current", current));
    }

    public ItemStack previousPage(int currentPage, int maxPage) {
        return build(previousPage, pagePlaceholders(currentPage, maxPage));
    }

    public ItemStack nextPage(int currentPage, int maxPage) {
        return build(nextPage, pagePlaceholders(currentPage, maxPage));
    }

    public ItemStack back() {
        return build(back, Map.of());
    }

    private Map<String, String> pagePlaceholders(int currentPage, int maxPage) {
        return Map.of(
            "page", String.valueOf(currentPage + 1),
            "max_page", String.valueOf(Math.max(1, maxPage + 1))
        );
    }

    private ItemStack build(Button button, Map<String, String> placeholders) {
        ItemStack item = new ItemStack(material(button.material()));
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(replace(button.name(), placeholders));
        List<String> lore = new ArrayList<>();
        for (String line : button.lore()) {
            lore.add(replace(line, placeholders));
        }
        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    private Material material(String name) {
        if (name != null && !name.isBlank()) {
            Material matched = Material.matchMaterial(name.trim().toUpperCase(Locale.ROOT));
            if (matched != null && matched.isItem()) {
                return matched;
            }
        }
        return Material.PAPER;
    }

    private String replace(String text, Map<String, String> placeholders) {
        if (text == null) {
            return "";
        }
        String replaced = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            replaced = replaced.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return replaced;
    }

    record Button(String material, String name, List<String> lore) {
    }
}
