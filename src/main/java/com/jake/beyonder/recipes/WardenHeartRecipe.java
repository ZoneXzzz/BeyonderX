package com.jake.beyonder.recipes;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenShardItem;
import com.jake.beyonder.items.WardenHeartItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.RecipeChoice;

public class WardenHeartRecipe {

    private final Beyonder plugin;
    private final NamespacedKey recipeKey;

    public WardenHeartRecipe(Beyonder plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "warden_heart");
    }

    public void registerRecipe() {
        // Remove existing recipe if it exists
        plugin.getServer().removeRecipe(recipeKey);

        FileConfiguration config = plugin.getConfig();

        // Only register if crafting is enabled
        if (!config.getBoolean("crafting.enabled", true)) {
            return;
        }

        // Get the warden heart item
        WardenHeartItem heartItem = plugin.getWardenHeartItem();
        ItemStack wardenHeart = heartItem.createHeart();

        // Create shaped recipe
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, wardenHeart);

        // Set recipe shape from config
        String[] shape = config.getStringList("crafting.warden-heart.shape").toArray(new String[0]);
        if (shape.length == 0) {
            // Default shape if not configured
            shape = new String[]{"SSS", "SHS", "SSS"};
        }
        recipe.shape(shape);

        // Set recipe ingredients
        WardenShardItem shardItem = plugin.getWardenShardItem();
        ItemStack wardenShard = shardItem.createShard(1);

        // S = Warden Shard
        recipe.setIngredient('S', new RecipeChoice.ExactChoice(wardenShard));
        // H = Heart of the Sea
        recipe.setIngredient('H', Material.HEART_OF_THE_SEA);

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("Warden Heart crafting recipe registered!");
    }
}