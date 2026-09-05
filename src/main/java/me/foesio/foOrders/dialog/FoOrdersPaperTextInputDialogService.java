package me.foesio.foOrders.dialog;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.dialog.DialogResponseView;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.DialogRegistryEntry;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import me.foesio.core.dialog.DialogButton;
import me.foesio.core.dialog.DialogIcons;
import me.foesio.core.dialog.NativeDialogSupport;
import me.foesio.core.dialog.TextDialogRequest;
import me.foesio.core.scheduler.FoScheduler;
import me.foesio.foOrders.FoOrders;
import me.foesio.foOrders.util.TextFormat;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public final class FoOrdersPaperTextInputDialogService implements FoOrdersTextInputDialogService {
    private static final String INPUT_KEY = "value";
    private static final int DEFAULT_BODY_WIDTH = 320;
    private static final int DEFAULT_INPUT_WIDTH = 300;
    private static final int DEFAULT_MAX_LENGTH = 128;
    private static final int DEFAULT_BUTTON_WIDTH = 120;
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();
    // The player answers a prompt once, and a stale callback must not reapply an
    // old value to a draft they have since changed.
    private static final ClickCallback.Options CALLBACK_OPTIONS = ClickCallback.Options.builder()
        .uses(1)
        .lifetime(Duration.ofMinutes(10))
        .build();

    private final FoOrders plugin;
    private final NativeDialogSupport support;
    private final FoScheduler scheduler;

    public FoOrdersPaperTextInputDialogService(FoOrders plugin, NativeDialogSupport support, FoScheduler scheduler) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
        this.support = Objects.requireNonNull(support, "support");
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    }

    @Override
    public boolean open(Player player, TextDialogRequest request, Consumer<String> onSubmit, Runnable onCancel) {
        if (player == null || !player.isOnline() || request == null || !support.canUseNativeDialogs()) {
            return false;
        }

        try {
            Dialog dialog = Dialog.create(factory -> {
                DialogRegistryEntry.Builder builder = factory.empty();
                builder.base(base(request));
                builder.type(DialogType.confirmation(
                    submitButton(player, request, onSubmit),
                    cancelButton(player, request, onCancel)
                ));
            });
            ((Audience) player).showDialog(dialog);
            return true;
        } catch (RuntimeException | LinkageError exception) {
            support.disableForSession("FoOrders text input dialog failed: " + exception.getMessage());
            String message = "Native text input dialog failed: " + exception.getMessage();
            plugin.getLogger().warning(message);
            if (plugin.fileLogger() != null) {
                plugin.fileLogger().warn(message);
            }
            return false;
        }
    }

    private DialogBase base(TextDialogRequest request) {
        Component title = component(request.title());
        List<DialogBody> body = new ArrayList<>();
        if (request.body() != null) {
            for (String line : request.body()) {
                if (line != null && !line.isBlank()) {
                    body.add(DialogBody.plainMessage(component(line), positive(request.bodyWidth(), DEFAULT_BODY_WIDTH)));
                }
            }
        }

        return DialogBase.builder(title)
            .externalTitle(title)
            .canCloseWithEscape(request.canCloseWithEscape())
            .pause(request.pause())
            .afterAction(DialogBase.DialogAfterAction.CLOSE)
            .body(List.copyOf(body))
            .inputs(List.of(DialogInput.text(INPUT_KEY, component(request.fieldLabel()))
                .width(positive(request.inputWidth(), DEFAULT_INPUT_WIDTH))
                .labelVisible(request.labelVisible())
                .initial(safe(request.initialValue()))
                .maxLength(positive(request.maxLength(), DEFAULT_MAX_LENGTH))
                .build()))
            .build();
    }

    private ActionButton submitButton(Player player, TextDialogRequest request, Consumer<String> onSubmit) {
        DialogButton button = request.submitButton();
        UUID playerId = player.getUniqueId();
        return ActionButton.create(
            buttonLabel(button, "Save"),
            tooltip(button),
            buttonWidth(button),
            DialogAction.customClick(
                (response, audience) -> {
                    String value = readValue(response);
                    runForPlayer(playerId, audience, () -> {
                        if (onSubmit != null) {
                            onSubmit.accept(value);
                        }
                    });
                },
                CALLBACK_OPTIONS
            )
        );
    }

    private ActionButton cancelButton(Player player, TextDialogRequest request, Runnable onCancel) {
        DialogButton button = request.cancelButton();
        UUID playerId = player.getUniqueId();
        return ActionButton.create(
            buttonLabel(button, "Cancel"),
            tooltip(button),
            buttonWidth(button),
            DialogAction.customClick(
                (response, audience) -> runForPlayer(playerId, audience, onCancel),
                CALLBACK_OPTIONS
            )
        );
    }

    private Component buttonLabel(DialogButton button, String fallback) {
        if (button == null) {
            return component(fallback);
        }
        String label = button.label();
        if (label == null || label.isBlank()) {
            label = fallback;
        }
        String icon = button.icon();
        return component(icon == null || icon.isBlank() ? label : DialogIcons.withIcon(label, icon));
    }

    private Component tooltip(DialogButton button) {
        if (button == null) {
            return null;
        }
        String tooltip = button.tooltip();
        return tooltip == null || tooltip.isBlank() ? null : component(tooltip);
    }

    private int buttonWidth(DialogButton button) {
        return button == null ? DEFAULT_BUTTON_WIDTH : positive(button.width(), DEFAULT_BUTTON_WIDTH);
    }

    private String readValue(DialogResponseView response) {
        if (response == null) {
            return "";
        }
        String value = response.getText(INPUT_KEY);
        return value == null ? "" : value;
    }

    private void runForPlayer(UUID playerId, Audience audience, Runnable action) {
        if (action == null || !(audience instanceof Player player) || !player.getUniqueId().equals(playerId)) {
            return;
        }
        scheduler.runForPlayer(player, () -> {
            if (plugin.isEnabled() && player.isOnline() && player.getUniqueId().equals(playerId)) {
                action.run();
            }
        });
    }

    private Component component(String text) {
        return DialogIcons.inlineTokens(LEGACY.deserialize(TextFormat.colorize(safe(text))));
    }

    private int positive(int value, int fallback) {
        return value > 0 ? value : fallback;
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }
}
