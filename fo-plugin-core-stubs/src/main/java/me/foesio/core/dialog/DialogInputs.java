package me.foesio.core.dialog;

import me.foesio.core.scheduler.FoScheduler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class DialogInputs {
    private DialogInputs() {}

    public static DialogInputs create(Plugin plugin, NativeDialogSupport support, FoScheduler scheduler) {
        throw new UnsupportedOperationException("stub");
    }

    public TextInput textInput() {
        throw new UnsupportedOperationException("stub");
    }

    public void clear(Player player) {
        throw new UnsupportedOperationException("stub");
    }

    public void close() {
        throw new UnsupportedOperationException("stub");
    }

    public static class TextInput {
        public void openTextInput(
            Player player,
            TextDialogRequest request,
            Consumer<String> onSubmit,
            Runnable onCancel
        ) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
