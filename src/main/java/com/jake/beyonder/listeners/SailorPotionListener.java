package com.jake.beyonder.listeners;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.SailorPotionItem;
import com.jake.beyonder.managers.PlayerAbilityManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class SailorPotionListener implements Listener {

    private final Beyonder plugin;
    private final SailorPotionItem sailorPotionItem;
    private final PlayerAbilityManager abilityManager;

    public SailorPotionListener(Beyonder plugin, PlayerAbilityManager abilityManager) {
        this.plugin = plugin;
        this.sailorPotionItem = plugin.getSailorPotionItem();
        this.abilityManager = abilityManager;
    }

    @EventHandler
    public void onSailorPotionConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        // Check if the consumed item is a Sailor Potion
        if (sailorPotionItem.isSailorPotion(item)) {
            // Grant the sailor abilities
            abilityManager.grantSailorAbilities(player);

            plugin.getLogger().info(player.getName() + " consumed Sailor Potion and gained sea-faring abilities");
        }
    }
}