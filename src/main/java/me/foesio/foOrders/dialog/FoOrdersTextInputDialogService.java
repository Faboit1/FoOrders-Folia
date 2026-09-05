package me.foesio.foOrders.dialog;

import me.foesio.core.dialog.TextDialogRequest;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

/**
 * Opens a single-field text dialog: the amount and price prompts in the new
 * order flow, and the order and item search prompts.
 */
public interface FoOrdersTextInputDialogService {
    /**
     * Shows the dialog.
     *
     * @return whether it was actually shown; false means the caller should fall
     *     back rather than assume the player was prompted.
     */
    boolean open(Player player, TextDialogRequest request, Consumer<String> onSubmit, Runnable onCancel);
}
