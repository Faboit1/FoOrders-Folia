package me.foesio.core.gui;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class EntryBrowserRequest {
    private EntryBrowserRequest() {}

    public static Builder builder() {
        return new Builder();
    }

    public int page() {
        throw new UnsupportedOperationException("stub");
    }

    public Object context() {
        throw new UnsupportedOperationException("stub");
    }

    public EntryBrowserRequest withPage(int page) {
        throw new UnsupportedOperationException("stub");
    }

    public static class Entry {
        private Entry() {}

        public static Entry of(String id, ItemStack item) {
            throw new UnsupportedOperationException("stub");
        }
    }

    public static class Builder {
        public Builder title(String title) {
            return this;
        }

        public Builder entries(List<Entry> entries) {
            return this;
        }

        public Builder page(int page) {
            return this;
        }

        public Builder filter(String filter) {
            return this;
        }

        public Builder buttons(GuiButtonConfig buttons) {
            return this;
        }

        public Builder showBack(boolean showBack) {
            return this;
        }

        public Builder addButton(ItemStack addButton) {
            return this;
        }

        public Builder emptyItem(ItemStack emptyItem) {
            return this;
        }

        public Builder context(Object context) {
            return this;
        }

        public EntryBrowserRequest build() {
            throw new UnsupportedOperationException("stub");
        }
    }
}
