package com.jake.beyonder.listeners;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.SpectatorPotionItem;
import com.jake.beyonder.managers.PlayerAbilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatorPotionListener implements Listener {

    private final Beyonder plugin;
    private final SpectatorPotionItem spectatorPotionItem;
    private final PlayerAbilityManager abilityManager;

    public SpectatorPotionListener(Beyonder plugin, PlayerAbilityManager abilityManager) {
        this.plugin = plugin;
        this.spectatorPotionItem = plugin.getSpectatorPotionItem();
        this.abilityManager = abilityManager;
    }

    @EventHandler
    public void onSpectatorPotionConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        // Check if the consumed item is a Spectator Potion
        if (spectatorPotionItem.isSpectatorPotion(item)) {
            // Grant the permanent spectator vision ability
            abilityManager.grantSpectatorVision(player);

            plugin.getLogger().info(player.getName() + " consumed Spectator Potion and gained permanent vision ability");
        }
    }
}