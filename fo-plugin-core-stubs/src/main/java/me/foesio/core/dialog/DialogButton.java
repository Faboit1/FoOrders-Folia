package me.foesio.core.dialog;

public class DialogButton {
    public static final String SAVE_ICON = "floppy_disk";
    public static final String CONFIRM_ICON_COLOR = "§a";

    private DialogButton() {}

    public static DialogButton search(String label, String tooltip, int width) {
        throw new UnsupportedOperationException("stub");
    }

    public static DialogButton cancel(String label, String tooltip, int width) {
        throw new UnsupportedOperationException("stub");
    }

    public static DialogButton icon(String iconName, String label, String tooltip, int width) {
        throw new UnsupportedOperationException("stub");
    }

    public String label() {
        throw new UnsupportedOperationException("stub");
    }

    public String labelWithIcon() {
        throw new UnsupportedOperationException("stub");
    }

    public String tooltip() {
        throw new UnsupportedOperationException("stub");
    }

    public int width() {
        throw new UnsupportedOperationException("stub");
    }

    public String icon() {
        throw new UnsupportedOperationException("stub");
    }
}
