package com.atlasgong.invisibleitemframeslite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigurationManager {

    private final Logger logger;
    private final FileConfiguration config;

    public ConfigurationManager(Logger logger, FileConfiguration config) {
        this.logger = logger;
        this.config = config;
    }

    /**
     * Adds default configuration values for a specific invisible item frame type.
     *
     * @param id   The identifier for the item type (e.g., "invisible_item_frame").
     * @param type The {@link Material} for the frame (e.g. Material.ITEM_FRAME).
     */
    public void addConfigDefaults(String id, Material type) {
        config.addDefault("items." + id + ".name", "Invisible " + Utils.toTitleCase(type.name().toLowerCase().replace("_", " ")));
        config.addDefault("items." + id + ".enchantment_glint", true);
        config.addDefault("recipes." + id + ".count", 8);
        config.addDefault("recipes." + id + ".shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.addDefault("recipes." + id + ".ingredients.F", "minecraft:" + type.name().toLowerCase());
        config.addDefault("recipes." + id + ".ingredients.A", "minecraft:phantom_membrane");
    }

    /**
     * Loads frame data from a given configuration section, deserializing text using MiniMessage.
     * The client code is expected to call `addConfigDefaults()` before this function.
     *
     * @param itemId The item ID for the corresponding configuration section.
     * @return An {@link ItemFrameData} object containing the name, lore, and glint flag.
     */
    public ItemFrameData loadItemData(String itemId) {
        ConfigurationSection section = config.getConfigurationSection("items." + itemId);
        if (section == null) {
            throw new IllegalArgumentException("Missing configuration section for 'items." + itemId + "'.");
        }

        // get name, lore, and glint from config
        String name = section.getString("name");
        assert name != null;
        List<String> lore = section.getStringList("lore");
        boolean glint = section.getBoolean("enchantment_glint");

        // translate strings to components

        // get legacy component serializer for legacy config
        LegacyComponentSerializer lcs = LegacyComponentSerializer.legacy('§');

        Component nameComponent;
        if (name.contains("§")) { // if string incl legacy characters
            nameComponent = lcs.deserialize(name);
        } else {
            nameComponent = MiniMessage.miniMessage().deserialize(name);
        }

        List<Component> loreComponents = lore.stream()
                .map(line -> {
                    if (line.contains("§")) {
                        return lcs.deserialize(line);
                    }
                    return MiniMessage.miniMessage().deserialize(line);
                })
                .toList();

        return new ItemFrameData(nameComponent, loreComponents, glint);
    }

    /**
     * Loads a Bukkit recipe from a given configuration section.
     * The client code is expected to call `addConfigDefaults()` before this function.
     *
     * @param recipeKey The unique namespaced key identifier for the recipe.
     * @param itemId    The item ID for the corresponding configuration section.
     * @param result    The item the recipe is expected to craft.
     */
    public Recipe loadRecipe(NamespacedKey recipeKey, String itemId, ItemStack result) {
        ConfigurationSection section = config.getConfigurationSection("recipes." + itemId);
        if (section == null) {
            throw new IllegalArgumentException("Missing configuration section for 'recipes." + itemId + "'.");
        }

        ItemStack item = result.clone();
        int count = section.getInt("count");
        if (count == 0) {
            logger.severe("Recipe count of " + itemId + " is 0. Falling back to 1.");
            count = 1;
        }
        item.setAmount(count);
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, item);
        List<String> shape = section.getStringList("shape");
        if (shape.isEmpty()) {
            logger.severe("Recipe shape is empty. Cannot continue.");
            return null;
        }
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = section.getConfigurationSection("ingredients");
        // if this is null, then the defaults above are incorrect.
        assert ingredients != null;
        for (Map.Entry<String, Object> entry : ingredients.getValues(false).entrySet()) {
            Material material = Material.matchMaterial(entry.getValue().toString());
            if (material == null) {
                logger.severe("Failed to find material '" + entry.getValue().toString() + "'. Skipping it and " +
                        "continuing.");
                continue;
            }
            recipe.setIngredient(entry.getKey().charAt(0), material);
        }

        return recipe;
    }
}
