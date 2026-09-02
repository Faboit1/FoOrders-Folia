package me.foesio.core;

import me.foesio.core.dialog.DialogService;
import me.foesio.core.inventory.InventoryCloseSuppressor;
import me.foesio.core.inventory.InventoryDepositService;
import me.foesio.core.scheduler.FoScheduler;
import me.foesio.core.update.UpdateNoticeService;
import org.bukkit.plugin.java.JavaPlugin;

public class FoCoreContext {
    private final JavaPlugin plugin;
    private final FoScheduler scheduler;
    private final DialogService dialogService;
    private final DialogSpriteSupport dialogSpriteSupport;
    private final InventoryCloseSuppressor inventoryCloseSuppressor = new InventoryCloseSuppressor();
    private final InventoryDepositService inventoryDepositService = new InventoryDepositService();

    public FoCoreContext() {
        this(null);
    }

    public FoCoreContext(JavaPlugin plugin) {
        this.plugin = plugin;
        this.scheduler = new FoScheduler(plugin);
        this.dialogService = new DialogService(plugin);
        this.dialogSpriteSupport = new DialogSpriteSupport();
    }

    public FoScheduler scheduler() {
        return scheduler;
    }

    public void warnIfNativeDialogsUnavailable() {
        if (plugin == null) {
            return;
        }
        var support = dialogService.support();
        if (support.canUseNativeDialogs() || !support.warnOnFallback()) {
            return;
        }
        String reason = support.unavailableReason();
        plugin.getLogger().warning(
            "Native dialogs are unavailable, falling back to inventory menus"
                + (reason == null ? "." : ": " + reason)
        );
    }

    public boolean supportsDialogSprites() {
        return dialogSpriteSupport.available();
    }

    public DialogSpriteSupport dialogSpriteSupport() {
        return dialogSpriteSupport;
    }

    public void metrics(int pluginId) {}

    public DialogService dialogService() {
        return dialogService;
    }

    public InventoryCloseSuppressor inventoryCloseSuppressor() {
        return inventoryCloseSuppressor;
    }

    public InventoryDepositService inventoryDeposits() {
        return inventoryDepositService;
    }

    public UpdateNoticeService createUpdateNotices(UpdateNoticeService.UpdateMessenger messenger, String projectId) {
        return new UpdateNoticeService();
    }

    public void close() {}
}
