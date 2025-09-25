package com.jake.beyonder.recipes;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenNullPotionItem;
import com.jake.beyonder.items.SailorPotionItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.RecipeChoice;

public class SailorPotionRecipe {

    private final Beyonder plugin;
    private final NamespacedKey recipeKey;

    public SailorPotionRecipe(Beyonder plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "sailor_potion");
    }

    public void registerRecipe() {
        // Remove existing recipe if it exists
        plugin.getServer().removeRecipe(recipeKey);

        FileConfiguration config = plugin.getConfig();

        // Only register if crafting is enabled
        if (!config.getBoolean("crafting.enabled", true)) {
            return;
        }

        // Get the sailor potion item
        SailorPotionItem potionItem = plugin.getSailorPotionItem();
        ItemStack sailorPotion = potionItem.createPotion();

        // Create shaped recipe
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, sailorPotion);

        // Set recipe shape from config
        String[] shape = config.getStringList("crafting.sailor-potion.shape").toArray(new String[0]);
        if (shape.length == 0) {
            // 3x3 grid with warden null potion in center
            shape = new String[]{"WTM", "BXN", "CHS"};
        }
        recipe.shape(shape);

        // Set recipe ingredients
        WardenNullPotionItem nullPotionItem = plugin.getWardenNullPotionItem();
        ItemStack wardenNullPotion = nullPotionItem.createPotion();

        // X = Warden Null Potion (center)
        recipe.setIngredient('X', new RecipeChoice.ExactChoice(wardenNullPotion));

        // Surrounding nautical ingredients
        recipe.setIngredient('W', Material.WATER_BUCKET);        // Water Bucket
        recipe.setIngredient('T', Material.TRIDENT);             // Trident
        recipe.setIngredient('M', Material.MAP);                 // Map
        recipe.setIngredient('B', Material.OAK_BOAT);            // Boat (any boat type)
        recipe.setIngredient('N', Material.NAUTILUS_SHELL);      // Nautilus Shell
        recipe.setIngredient('C', Material.COMPASS);             // Compass
        recipe.setIngredient('H', Material.HEART_OF_THE_SEA);    // Heart of the Sea
        recipe.setIngredient('S', Material.WRITTEN_BOOK);        // Written Book

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("Sailor Potion crafting recipe registered!");
    }
}