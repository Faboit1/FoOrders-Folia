package me.foesio.core.discord;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.function.Supplier;

public class DiscordWebhookService {
    private DiscordWebhookService() {}

    public static DiscordWebhookService create(JavaPlugin plugin, Supplier<DiscordWebhookSettings> settingsSupplier) {
        throw new UnsupportedOperationException("stub");
    }

    public void sendEmbed(String eventKey, DiscordWebhookEmbed embed) {
        throw new UnsupportedOperationException("stub");
    }
}
