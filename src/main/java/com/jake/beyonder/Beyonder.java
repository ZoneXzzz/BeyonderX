package com.jake.beyonder;

import com.jake.beyonder.commands.BeyonderCommand;
import com.jake.beyonder.items.*;
import com.jake.beyonder.listeners.*;
import com.jake.beyonder.managers.PlayerAbilityManager;
import com.jake.beyonder.recipes.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Beyonder extends JavaPlugin {

    private static Beyonder instance;
    private WardenShardItem wardenShardItem;
    private WardenHeartItem wardenHeartItem;
    private WardenNullPotionItem wardenNullPotionItem;
    private SpectatorPotionItem spectatorPotionItem;
    private TelepathistPotionItem telepathistPotionItem;
    private SailorPotionItem sailorPotionItem;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config
        saveDefaultConfig();
        config = getConfig();

        // Initialize the custom items
        initializeItems();

        // Register recipes if crafting is enabled
        registerRecipes();

        // Register events and listeners
        registerListeners();

        // Register commands
        registerCommands();

        getLogger().info("Beyonder plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("Beyonder plugin has been disabled!");
    }

    private void initializeItems() {
        wardenShardItem = new WardenShardItem(config);
        wardenHeartItem = new WardenHeartItem(config);
        wardenNullPotionItem = new WardenNullPotionItem(config);
        spectatorPotionItem = new SpectatorPotionItem(config);
        telepathistPotionItem = new TelepathistPotionItem(config);
        sailorPotionItem = new SailorPotionItem(config);

        getLogger().info("Custom items initialized!");
    }

    private void registerRecipes() {
        if (config.getBoolean("crafting.enabled", true)) {
            new WardenHeartRecipe(this).registerRecipe();
            new WardenNullPotionRecipe(this).registerRecipe();
            new SpectatorPotionRecipe(this).registerRecipe();
            new TelepathistPotionRecipe(this).registerRecipe();
            new SailorPotionRecipe(this).registerRecipe();
            getLogger().info("All crafting recipes registered!");
        } else {
            getLogger().info("Crafting is disabled in config");
        }
    }

    private void registerListeners() {
        // Create ability manager first
        PlayerAbilityManager abilityManager = new PlayerAbilityManager(this);

        // Register all listeners
        getServer().getPluginManager().registerEvents(new WardenDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(this), this);
        getServer().getPluginManager().registerEvents(new PotionConsumeListener(this), this);
        getServer().getPluginManager().registerEvents(new SpectatorPotionListener(this, abilityManager), this);
        getServer().getPluginManager().registerEvents(new TelepathistPotionListener(this, abilityManager), this);
        getServer().getPluginManager().registerEvents(new SailorPotionListener(this, abilityManager), this);
        getServer().getPluginManager().registerEvents(abilityManager, this);

        getLogger().info("All event listeners registered!");
    }

    private void registerCommands() {
        getCommand("beyonder").setExecutor(new BeyonderCommand(this));
        getLogger().info("Commands registered!");
    }

    public static Beyonder getInstance() {
        return instance;
    }

    // Getter methods for all items
    public WardenShardItem getWardenShardItem() {
        return wardenShardItem;
    }

    public WardenHeartItem getWardenHeartItem() {
        return wardenHeartItem;
    }

    public WardenNullPotionItem getWardenNullPotionItem() {
        return wardenNullPotionItem;
    }

    public SpectatorPotionItem getSpectatorPotionItem() {
        return spectatorPotionItem;
    }

    public TelepathistPotionItem getTelepathistPotionItem() {
        return telepathistPotionItem;
    }

    public SailorPotionItem getSailorPotionItem() {
        return sailorPotionItem;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        config = getConfig();

        // Reload all item configurations
        wardenShardItem.reloadConfig(config);
        wardenHeartItem.reloadConfig(config);
        wardenNullPotionItem.reloadConfig(config);
        spectatorPotionItem.reloadConfig(config);
        telepathistPotionItem.reloadConfig(config);
        sailorPotionItem.reloadConfig(config);

        // Re-register recipes if crafting is enabled
        if (config.getBoolean("crafting.enabled", true)) {
            new WardenHeartRecipe(this).registerRecipe();
            new WardenNullPotionRecipe(this).registerRecipe();
            new SpectatorPotionRecipe(this).registerRecipe();
            new TelepathistPotionRecipe(this).registerRecipe();
            new SailorPotionRecipe(this).registerRecipe();
        }

        getLogger().info("Beyonder configuration reloaded!");
    }
}