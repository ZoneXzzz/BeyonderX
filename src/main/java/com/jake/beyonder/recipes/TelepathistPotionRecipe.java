package com.jake.beyonder.recipes;

import com.jake.beyonder.Beyonder;
import com.jake.beyonder.items.SpectatorPotionItem;
import com.jake.beyonder.items.TelepathistPotionItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.RecipeChoice;

public class TelepathistPotionRecipe {

    private final Beyonder plugin;
    private final NamespacedKey recipeKey;

    public TelepathistPotionRecipe(Beyonder plugin) {
        this.plugin = plugin;
        this.recipeKey = new NamespacedKey(plugin, "telepathist_potion");
    }

    public void registerRecipe() {
        // Remove existing recipe if it exists
        plugin.getServer().removeRecipe(recipeKey);

        FileConfiguration config = plugin.getConfig();

        // Only register if crafting is enabled
        if (!config.getBoolean("crafting.enabled", true)) {
            return;
        }

        // Get the telepathist potion item
        TelepathistPotionItem potionItem = plugin.getTelepathistPotionItem();
        ItemStack telepathistPotion = potionItem.createPotion();

        // Create shaped recipe
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, telepathistPotion);

        // Set recipe shape from config
        String[] shape = config.getStringList("crafting.telepathist-potion.shape").toArray(new String[0]);
        if (shape.length == 0) {
            // Simple shape: 8 prismarine crystals around spectator potion
            shape = new String[]{"PPP", "PSP", "PPP"};
        }
        recipe.shape(shape);

        // Set recipe ingredients
        SpectatorPotionItem spectatorPotionItem = plugin.getSpectatorPotionItem();
        ItemStack spectatorPotion = spectatorPotionItem.createPotion();

        // S = Spectator Potion (center)
        recipe.setIngredient('S', new RecipeChoice.ExactChoice(spectatorPotion));

        // P = Prismarine Crystals (surrounding)
        recipe.setIngredient('P', Material.PRISMARINE_CRYSTALS);

        // Register the recipe
        plugin.getServer().addRecipe(recipe);
        plugin.getLogger().info("Telepathist Potion crafting recipe registered!");
    }
}