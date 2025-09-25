package com.jake.beyonder.listeners;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenShardItem;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Warden;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class WardenDeathListener implements Listener {

    private final Beyonder plugin;
    private final WardenShardItem wardenShardItem;
    private final Random random;

    public WardenDeathListener(Beyonder plugin) {
        this.plugin = plugin;
        this.wardenShardItem = plugin.getWardenShardItem();
        this.random = new Random();
    }

    @EventHandler
    public void onWardenDeath(EntityDeathEvent event) {
        // Check if the entity is a Warden
        if (event.getEntityType() != EntityType.WARDEN) {
            return;
        }

        Warden warden = (Warden) event.getEntity();

        // Check drop chance
        int dropChance = wardenShardItem.getDropChance();
        if (random.nextInt(100) >= dropChance) {
            return; // Drop chance failed
        }

        // Determine drop amount
        int minAmount = wardenShardItem.getMinAmount();
        int maxAmount = wardenShardItem.getMaxAmount();
        int amount = minAmount + random.nextInt(maxAmount - minAmount + 1);

        // Create and drop the warden shard
        ItemStack shard = wardenShardItem.createShard(amount);
        warden.getWorld().dropItemNaturally(warden.getLocation(), shard);

        plugin.getLogger().info("Warden Shard dropped! Amount: " + amount);
    }
}