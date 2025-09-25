package com.jake.beyonder.recipes;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.WardenNullPotionItem;
import com.jake.beyonder.items.SpectatorPotionItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.RecipeChoice;

public class SpectatorPotionRecipe {

    private final Beyonder plugin;
    private final NamespacedKey recipeKey;

    public SpectatorPotionRecipe(Beyonder plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "spectator_potion");
    }

    public void registerRecipe() {
        // Remove existing recipe if it exists
        plugin.getServer().removeRecipe(recipeKey);

        FileConfiguration config = plugin.getConfig();

        // Only register if crafting is enabled
        if (!config.getBoolean("crafting.enabled", true)) {
            return;
        }

        // Get the spectator potion item
        SpectatorPotionItem potionItem = plugin.getSpectatorPotionItem();
        ItemStack spectatorPotion = potionItem.createPotion();

        // Create shaped recipe
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, spectatorPotion);

        // Set recipe shape from config
        String[] shape = config.getStringList("crafting.spectator-potion.shape").toArray(new String[0]);
        if (shape.length == 0) {
            // Default epic crafting shape
            shape = new String[]{"BNQPGT", "C X  F", "MMMMMMMM"};
        }
        recipe.shape(shape);

        // Set recipe ingredients
        WardenNullPotionItem nullPotionItem = plugin.getWardenNullPotionItem();
        ItemStack wardenNullPotion = nullPotionItem.createPotion();

        // X = Warden Null Potion (center)
        recipe.setIngredient('X', new RecipeChoice.ExactChoice(wardenNullPotion));

        // Surrounding ingredients
        recipe.setIngredient('B', Material.BOOK);           // Book
        recipe.setIngredient('N', Material.NAME_TAG);       // Name Tag
        recipe.setIngredient('Q', Material.QUARTZ);         // Nether Quartz
        recipe.setIngredient('P', Material.PAPER);          // Paper
        recipe.setIngredient('G', Material.GHAST_TEAR);     // Ghast Tear
        recipe.setIngredient('T', Material.CLOCK);          // Clock
        recipe.setIngredient('C', Material.FEATHER);        // Feather (left)
        recipe.setIngredient('F', Material.FEATHER);        // Feather (right)

        // M = Any item (air means any item can fill these slots)
        recipe.setIngredient('M', new RecipeChoice.MaterialChoice(
                Material.AIR, Material.REDSTONE, Material.GLOWSTONE_DUST,
                Material.GUNPOWDER, Material.DRAGON_BREATH
        ));

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("Spectator Potion epic crafting recipe registered!");
    }
}