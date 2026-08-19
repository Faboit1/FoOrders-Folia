package me.foesio.core;

import me.foesio.core.dialog.DialogService;
import me.foesio.core.inventory.InventoryCloseSuppressor;
import me.foesio.core.inventory.InventoryDepositService;
import me.foesio.core.scheduler.FoScheduler;
import me.foesio.core.update.UpdateNoticeService;

public class FoCoreContext {
    public FoScheduler scheduler() {
        throw new UnsupportedOperationException("stub");
    }

    public void warnIfNativeDialogsUnavailable() {
        throw new UnsupportedOperationException("stub");
    }

    public boolean supportsDialogSprites() {
        throw new UnsupportedOperationException("stub");
    }

    public DialogSpriteSupport dialogSpriteSupport() {
        throw new UnsupportedOperationException("stub");
    }

    public void metrics(int pluginId) {
        throw new UnsupportedOperationException("stub");
    }

    public DialogService dialogService() {
        throw new UnsupportedOperationException("stub");
    }

    public InventoryCloseSuppressor inventoryCloseSuppressor() {
        throw new UnsupportedOperationException("stub");
    }

    public InventoryDepositService inventoryDeposits() {
        throw new UnsupportedOperationException("stub");
    }

    public UpdateNoticeService createUpdateNotices(UpdateNoticeService.UpdateMessenger messenger, String projectId) {
        throw new UnsupportedOperationException("stub");
    }

    public void close() {
        throw new UnsupportedOperationException("stub");
    }
}
