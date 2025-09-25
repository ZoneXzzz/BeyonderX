package com.jake.beyonder.listeners;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenHeartItem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {

    private final Beyonder plugin;
    private final WardenHeartItem wardenHeartItem;

    public CraftingListener(Beyonder plugin) {
        this.plugin = plugin;
        this.wardenHeartItem = plugin.getWardenHeartItem();
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        CraftingInventory inventory = event.getInventory();
        ItemStack result = inventory.getResult();

        // Check if the result is a Warden Heart
        if (result != null && wardenHeartItem.isWardenHeart(result)) {
            // Additional validation can be added here if needed
            plugin.getLogger().info("Warden Heart crafting attempt detected");
        }

        // You can add more crafting validations for other custom items here
    }
}