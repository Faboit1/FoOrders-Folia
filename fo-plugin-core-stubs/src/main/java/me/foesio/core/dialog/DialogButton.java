package me.foesio.core.dialog;

public class DialogButton {
    public static final String SAVE_ICON = "floppy_disk";
    public static final String CONFIRM_ICON_COLOR = "§a";

    private final String icon;
    private final String label;
    private final String tooltip;
    private final int width;

    DialogButton(String icon, String label, String tooltip, int width) {
        this.icon = icon == null ? "" : icon;
        this.label = label == null ? "" : label;
        this.tooltip = tooltip == null ? "" : tooltip;
        this.width = width;
    }

    public static DialogButton search(String label, String tooltip, int width) {
        return new DialogButton("magnifying_glass", label, tooltip, width);
    }

    public static DialogButton cancel(String label, String tooltip, int width) {
        return new DialogButton("barrier", label, tooltip, width);
    }

    public static DialogButton icon(String iconName, String label, String tooltip, int width) {
        return new DialogButton(iconName, label, tooltip, width);
    }

    public String label() {
        return label;
    }

    public String labelWithIcon() {
        return label;
    }

    public String tooltip() {
        return tooltip;
    }

    public int width() {
        return width;
    }

    public String icon() {
        return icon;
    }
}
