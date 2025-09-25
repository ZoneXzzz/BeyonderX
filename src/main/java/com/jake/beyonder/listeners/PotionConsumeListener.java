package com.jake.beyonder.listeners;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenNullPotionItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class PotionConsumeListener implements Listener {

    private final Beyonder plugin;
    private final WardenNullPotionItem wardenNullPotionItem;

    public PotionConsumeListener(Beyonder plugin) {
        this.plugin = plugin;
        this.wardenNullPotionItem = plugin.getWardenNullPotionItem();
    }

    @EventHandler
    public void onPotionConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        // Check if the consumed item is a Warden Null Potion
        if (wardenNullPotionItem.isWardenNullPotion(item)) {
            // Apply custom effects
            wardenNullPotionItem.applyEffects(player);

            // Send message to player
            player.sendMessage("§8You feel a wave of silence wash over you...");

            plugin.getLogger().info(player.getName() + " consumed a Warden Null Potion");
        }
    }
}