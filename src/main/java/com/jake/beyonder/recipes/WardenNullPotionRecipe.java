package com.jake.beyonder.recipes;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenHeartItem;
import com.jake.beyonder.items.WardenNullPotionItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.RecipeChoice;

public class WardenNullPotionRecipe {

    private final Beyonder plugin;
    private final NamespacedKey recipeKey;

    public WardenNullPotionRecipe(Beyonder plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "warden_null_potion");
    }

    public void registerRecipe() {
        // Remove existing recipe if it exists
        plugin.getServer().removeRecipe(recipeKey);

        FileConfiguration config = plugin.getConfig();

        // Only register if crafting is enabled
        if (!config.getBoolean("crafting.enabled", true)) {
            return;
        }

        // Get the warden null potion item
        WardenNullPotionItem potionItem = plugin.getWardenNullPotionItem();
        ItemStack wardenNullPotion = potionItem.createPotion();

        // Create shaped recipe
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, wardenNullPotion);

        // Set recipe shape from config
        String[] shape = config.getStringList("crafting.warden-null-potion.shape").toArray(new String[0]);
        if (shape.length == 0) {
            // Default shape if not configured
            shape = new String[]{"W", "B"};
        }
        recipe.shape(shape);

        // Set recipe ingredients
        WardenHeartItem heartItem = plugin.getWardenHeartItem();
        ItemStack wardenHeart = heartItem.createHeart();

        // W = Warden Heart
        recipe.setIngredient('W', new RecipeChoice.ExactChoice(wardenHeart));
        // B = Water Bottle
        recipe.setIngredient('B', Material.POTION);

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("Warden Null Potion crafting recipe registered!");
    }
}