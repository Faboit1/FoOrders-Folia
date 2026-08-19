package me.foesio.core.gui;

public class EntryBrowserClick {
    public Action action() {
        throw new UnsupportedOperationException("stub");
    }

    public String entryId() {
        throw new UnsupportedOperationException("stub");
    }

    public enum Action {
        ENTRY,
        ADD,
        SEARCH,
        CLEAR_SEARCH,
        PREVIOUS_PAGE,
        NEXT_PAGE,
        BACK,
        NONE
    }
}
