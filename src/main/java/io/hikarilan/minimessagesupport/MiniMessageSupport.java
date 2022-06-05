package io.hikarilan.minimessagesupport;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class MiniMessageSupport extends JavaPlugin implements Listener {

    private MiniMessage miniMessage = MiniMessage.miniMessage();

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onChat(AsyncChatEvent e) {
        e.message(fromMiniMessage(e.message()));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onChangeSign(SignChangeEvent e) {
        new ArrayList<>(e.lines()).forEach((component) -> {
            e.line(e.lines().indexOf(component), fromMiniMessage(component));
        });
    }

    private Component fromMiniMessage(Component message) {
        if (!message.children().isEmpty()) {
           return message.children(message.children().stream().map(this::fromMiniMessage).toList());
        }
        if (message instanceof TextComponent t) {
            return miniMessage.deserialize(t.content());
        }
        return message;
    }
}
