package io.hikarilan.minimessagesupport;

import com.destroystokyo.paper.event.block.AnvilDamagedEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
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

    @EventHandler
    private void onClickAnvil(InventoryClickEvent e) {
        if (e.getInventory().getType() != InventoryType.ANVIL) return;
        if (e.getSlot() != 2) return;
        AnvilInventory anvil = (AnvilInventory) e.getInventory();
        if (anvil.getRenameText() == null) return;
        if (anvil.getResult() == null) return;
        anvil.getResult().editMeta(it -> {
            it.displayName(fromMiniMessage(Component.text(anvil.getRenameText())));
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
