package com.jake.beyonder.listeners;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.TelepathistPotionItem;
import com.jake.beyonder.managers.PlayerAbilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class TelepathistPotionListener implements Listener {

    private final Beyonder plugin;
    private final TelepathistPotionItem telepathistPotionItem;
    private final PlayerAbilityManager abilityManager;

    public TelepathistPotionListener(Beyonder plugin, PlayerAbilityManager abilityManager) {
        this.plugin = plugin;
        this.telepathistPotionItem = plugin.getTelepathistPotionItem();
        this.abilityManager = abilityManager;
    }

    @EventHandler
    public void onTelepathistPotionConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        // Check if the consumed item is a Telepathist Potion
        if (telepathistPotionItem.isTelepathistPotion(item)) {
            // Check if player can consume it (must have Spectator ability)
            if (!abilityManager.canConsumeTelepathistPotion(player)) {
                event.setCancelled(true);
                player.sendMessage("§cYou must first gain Spectator Vision before consuming this potion!");
                player.sendMessage("§cDrink a Spectator Potion to unlock this ability.");
                return;
            }

            // Grant the telepathist vision ability
            abilityManager.grantTelepathistVision(player);

            plugin.getLogger().info(player.getName() + " consumed Telepathist Potion and gained health vision ability");
        }
    }
}