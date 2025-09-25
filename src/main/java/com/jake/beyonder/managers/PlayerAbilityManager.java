package com.jake.beyonder.managers;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.SpectatorPotionItem;
import com.jake.beyonder.items.TelepathistPotionItem;
import com.jake.beyonder.items.SailorPotionItem;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PlayerAbilityManager implements Listener {

    private final Beyonder plugin;
    private final SpectatorPotionItem spectatorPotionItem;
    private final TelepathistPotionItem telepathistPotionItem;
    private final SailorPotionItem sailorPotionItem;
    private final Set<UUID> playersWithSpectatorVision;
    private final Set<UUID> playersWithTelepathistVision;
    private final Set<UUID> playersWithSailorAbilities;

    private final Map<UUID, BukkitRunnable> visionTasks;
    private final Map<UUID, BukkitRunnable> healthDisplayTasks;
    private final Map<UUID, BukkitRunnable> sailorEffectTasks;

    public PlayerAbilityManager(Beyonder plugin) {
        this.plugin = plugin;
        this.spectatorPotionItem = plugin.getSpectatorPotionItem();
        this.telepathistPotionItem = plugin.getTelepathistPotionItem();
        this.sailorPotionItem = plugin.getSailorPotionItem();
        this.playersWithSpectatorVision = new HashSet<>();
        this.playersWithTelepathistVision = new HashSet<>();
        this.playersWithSailorAbilities = new HashSet<>();
        this.visionTasks = new HashMap<>();
        this.healthDisplayTasks = new HashMap<>();
        this.sailorEffectTasks = new HashMap<>();

        startVisionTask();
        startHealthDisplayTask();
        startSailorEffectsTask();

        plugin.getLogger().info("PlayerAbilityManager initialized!");
    }

    // Grant Sailor abilities to player
    public void grantSailorAbilities(Player player) {
        UUID playerId = player.getUniqueId();
        removeAllCustomAbilities(player);
        sailorPotionItem.grantSailorAbilities(player);
        playersWithSailorAbilities.add(playerId);
        applySailorEffects(player);
        plugin.getLogger().info("Granted Sailor Abilities to " + player.getName());
    }

    // Grant Telepathist ability to player
    public void grantTelepathistVision(Player player) {
        UUID playerId = player.getUniqueId();
        if (spectatorPotionItem.hasSpectatorVision(player)) {
            removeSpectatorVision(player);
        }
        if (sailorPotionItem.hasSailorAbilities(player)) {
            removeSailorAbilities(player);
        }
        telepathistPotionItem.grantTelepathistVision(player);
        playersWithTelepathistVision.add(playerId);
        applySpectatorVisionEffects(player);
        applyTelepathistVisionEffects(player);
        plugin.getLogger().info("Granted Telepathist Vision to " + player.getName());
    }

    // Grant Spectator ability to player
    public void grantSpectatorVision(Player player) {
        UUID playerId = player.getUniqueId();
        removeAllCustomAbilities(player);
        spectatorPotionItem.grantSpectatorVision(player);
        playersWithSpectatorVision.add(playerId);
        applySpectatorVisionEffects(player);
        plugin.getLogger().info("Granted Spectator Vision to " + player.getName());
    }

    // Remove all custom abilities from player
    public void removeAllCustomAbilities(Player player) {
        UUID playerId = player.getUniqueId();
        if (sailorPotionItem.hasSailorAbilities(player)) {
            removeSailorAbilities(player);
        }
        if (telepathistPotionItem.hasTelepathistVision(player)) {
            removeTelepathistVision(player);
        }
        if (spectatorPotionItem.hasSpectatorVision(player)) {
            removeSpectatorVision(player);
        }
        plugin.getLogger().info("Removed all custom abilities from " + player.getName());
    }

    // Remove specific abilities
    private void removeSpectatorVision(Player player) {
        UUID playerId = player.getUniqueId();
        spectatorPotionItem.removeSpectatorVision(player);
        playersWithSpectatorVision.remove(playerId);
        removeSpectatorVisionEffects(player);
    }

    private void removeTelepathistVision(Player player) {
        UUID playerId = player.getUniqueId();
        telepathistPotionItem.removeTelepathistVision(player);
        playersWithTelepathistVision.remove(playerId);
        removeTelepathistVisionEffects(player);
    }

    private void removeSailorAbilities(Player player) {
        UUID playerId = player.getUniqueId();
        sailorPotionItem.removeSailorAbilities(player);
        playersWithSailorAbilities.remove(playerId);
        removeSailorEffects(player);
    }

    // Apply Sailor effects
    private void applySailorEffects(Player player) {
        UUID playerId = player.getUniqueId();

        if (sailorEffectTasks.containsKey(playerId)) {
            sailorEffectTasks.get(playerId).cancel();
            sailorEffectTasks.remove(playerId);
        }

        BukkitRunnable sailorTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    sailorEffectTasks.remove(playerId);
                    return;
                }

                boolean isRaining = player.getWorld().hasStorm();
                double damageReduction = isRaining ? 0.10 : 0.05;

                // Apply night vision constantly
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, 0, true, false));

                // Apply water breathing when underwater
                if (player.isUnderwater()) {
                    int breathDuration = isRaining ? 600 : 400;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, breathDuration, 0, true, false));

                    // Apply dolphin's grace for faster swimming
                    int swimSpeed = isRaining ? 1 : 0;
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 100, swimSpeed, true, false));
                }

                // Store damage reduction in player's metadata for event handling
                player.setMetadata("sailor_damage_reduction", new org.bukkit.metadata.FixedMetadataValue(plugin, damageReduction));
            }
        };

        sailorTask.runTaskTimer(plugin, 0L, 40L); // Run every 2 seconds
        sailorEffectTasks.put(playerId, sailorTask);
    }

    // Apply Spectator Vision effects (see invisible players)
    private void applySpectatorVisionEffects(Player player) {
        UUID playerId = player.getUniqueId();

        if (visionTasks.containsKey(playerId)) {
            visionTasks.get(playerId).cancel();
            visionTasks.remove(playerId);
        }

        BukkitRunnable visionTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    visionTasks.remove(playerId);
                    return;
                }

                // Make invisible players visible to this player
                for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                    if (otherPlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        player.showPlayer(plugin, otherPlayer);
                    }
                }
            }
        };

        visionTask.runTaskTimer(plugin, 0L, 20L); // Run every second
        visionTasks.put(playerId, visionTask);
    }

    // Apply Telepathist Vision effects (health display)
    private void applyTelepathistVisionEffects(Player player) {
        UUID playerId = player.getUniqueId();

        if (healthDisplayTasks.containsKey(playerId)) {
            healthDisplayTasks.get(playerId).cancel();
            healthDisplayTasks.remove(playerId);
        }

        BukkitRunnable healthTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    this.cancel();
                    healthDisplayTasks.remove(playerId);
                    return;
                }

                // Display health information for all players
                for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                    if (!otherPlayer.equals(player)) {
                        displayPlayerHealth(player, otherPlayer);
                    }
                }
            }
        };

        healthTask.runTaskTimer(plugin, 0L, 20L); // Run every second
        healthDisplayTasks.put(playerId, healthTask);
    }

    // Display player health using action bar
    private void displayPlayerHealth(Player viewer, Player target) {
        double health = target.getHealth();
        double maxHealth = target.getMaxHealth();

        StringBuilder healthBar = new StringBuilder();
        int fullHearts = (int) (health / 2);
        int halfHeart = (health % 2 == 1) ? 1 : 0;
        int emptyHearts = (int) ((maxHealth - health) / 2);

        // Color based on health percentage
        ChatColor color;
        double healthPercent = health / maxHealth;
        if (healthPercent > 0.7) color = ChatColor.GREEN;
        else if (healthPercent > 0.3) color = ChatColor.YELLOW;
        else color = ChatColor.RED;

        // Build health bar
        for (int i = 0; i < fullHearts; i++) healthBar.append("❤");
        if (halfHeart == 1) healthBar.append("💛");
        for (int i = 0; i < emptyHearts; i++) healthBar.append("♡");

        // Send action bar message
        String message = color + target.getName() + ": " + healthBar.toString() +
                " §7(" + (int)health + "/" + (int)maxHealth + ")";
        viewer.sendActionBar(message);
    }

    // Remove effects
    private void removeSpectatorVisionEffects(Player player) {
        UUID playerId = player.getUniqueId();
        if (visionTasks.containsKey(playerId)) {
            visionTasks.get(playerId).cancel();
            visionTasks.remove(playerId);
        }

        // Reset player visibility to normal
        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
            if (otherPlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                player.hidePlayer(plugin, otherPlayer);
            }
        }
    }

    private void removeTelepathistVisionEffects(Player player) {
        UUID playerId = player.getUniqueId();
        if (healthDisplayTasks.containsKey(playerId)) {
            healthDisplayTasks.get(playerId).cancel();
            healthDisplayTasks.remove(playerId);
        }

        // Clear action bar
        player.sendActionBar("");
    }

    private void removeSailorEffects(Player player) {
        UUID playerId = player.getUniqueId();
        if (sailorEffectTasks.containsKey(playerId)) {
            sailorEffectTasks.get(playerId).cancel();
            sailorEffectTasks.remove(playerId);
        }

        // Remove potion effects
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
        player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);

        // Remove metadata
        player.removeMetadata("sailor_damage_reduction", plugin);
    }

    // Damage reduction event handler
    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if (playersWithSailorAbilities.contains(player.getUniqueId())) {
            // Check if player has damage reduction metadata
            if (player.hasMetadata("sailor_damage_reduction")) {
                double reduction = player.getMetadata("sailor_damage_reduction").get(0).asDouble();
                double originalDamage = event.getDamage();
                double reducedDamage = originalDamage * (1 - reduction);

                event.setDamage(reducedDamage);

                // Optional: Send message for debugging
                // player.sendMessage("§9Sailor Protection: Reduced damage from " +
                //                   originalDamage + " to " + reducedDamage);
            }
        }
    }

    // Start main vision tasks
    private void startVisionTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Update Spectator Vision for all players
                Set<UUID> allVisionPlayers = new HashSet<>();
                allVisionPlayers.addAll(playersWithSpectatorVision);
                allVisionPlayers.addAll(playersWithTelepathistVision);

                for (UUID playerId : allVisionPlayers) {
                    Player player = plugin.getServer().getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                            if (otherPlayer.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                                player.showPlayer(plugin, otherPlayer);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 40L); // Run every 2 seconds
    }

    private void startHealthDisplayTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : playersWithTelepathistVision) {
                    Player player = plugin.getServer().getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        // Display health for all other players
                        for (Player otherPlayer : plugin.getServer().getOnlinePlayers()) {
                            if (!otherPlayer.equals(player)) {
                                displayPlayerHealth(player, otherPlayer);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L); // Run every second
    }

    private void startSailorEffectsTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID playerId : playersWithSailorAbilities) {
                    Player player = plugin.getServer().getPlayer(playerId);
                    if (player != null && player.isOnline()) {
                        // Effects are handled in individual tasks, but we can add global checks here
                        boolean isRaining = player.getWorld().hasStorm();

                        // Update metadata for damage reduction
                        double damageReduction = isRaining ? 0.10 : 0.05;
                        player.setMetadata("sailor_damage_reduction",
                                new org.bukkit.metadata.FixedMetadataValue(plugin, damageReduction));
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 100L); // Run every 5 seconds
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Re-apply abilities if player has them (persistent across logins)
        if (sailorPotionItem.hasSailorAbilities(player)) {
            playersWithSailorAbilities.add(player.getUniqueId());
            applySailorEffects(player);
            player.sendMessage("§9Your Sailor Abilities have been restored!");
        } else if (telepathistPotionItem.hasTelepathistVision(player)) {
            playersWithTelepathistVision.add(player.getUniqueId());
            applySpectatorVisionEffects(player);
            applyTelepathistVisionEffects(player);
            player.sendMessage("§dYour Telepathist Vision has been restored!");
        } else if (spectatorPotionItem.hasSpectatorVision(player)) {
            playersWithSpectatorVision.add(player.getUniqueId());
            applySpectatorVisionEffects(player);
            player.sendMessage("§bYour Spectator Vision has been restored!");
        }
    }

    // Check if player can consume Telepathist potion
    public boolean canConsumeTelepathistPotion(Player player) {
        return spectatorPotionItem.hasSpectatorVision(player) &&
                !telepathistPotionItem.hasTelepathistVision(player);
    }

    // Utility methods for other classes to check abilities
    public boolean hasSpectatorVision(Player player) {
        return playersWithSpectatorVision.contains(player.getUniqueId());
    }

    public boolean hasTelepathistVision(Player player) {
        return playersWithTelepathistVision.contains(player.getUniqueId());
    }

    public boolean hasSailorAbilities(Player player) {
        return playersWithSailorAbilities.contains(player.getUniqueId());
    }
}