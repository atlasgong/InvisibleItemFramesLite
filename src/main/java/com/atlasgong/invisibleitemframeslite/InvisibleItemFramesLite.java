package com.atlasgong.invisibleitemframeslite;

import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameBreakListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameCraftListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameInteractionListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFramePlaceListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InvisibleItemFramesLite extends JavaPlugin {

    private static boolean firstLoad = true;

    public NamespacedKey getInvisibleKey() {
        return IS_INVISIBLE_KEY;
    }

    public NamespacedKey getRegularRecipeKey() {
        return REGULAR_RECIPE_KEY;
    }

    public NamespacedKey getGlowRecipeKey() {
        return GLOW_RECIPE_KEY;
    }

    public NamespacedKey getShapelessGlowRecipeKey() {
        return SHAPELESS_GLOW_RECIPE_KEY;
    }

    private final NamespacedKey IS_INVISIBLE_KEY;
    private final NamespacedKey REGULAR_RECIPE_KEY;
    private final NamespacedKey GLOW_RECIPE_KEY;
    private final NamespacedKey SHAPELESS_GLOW_RECIPE_KEY;

    public InvisibleItemFramesLite() {
        super();
        IS_INVISIBLE_KEY = new NamespacedKey(this, "invisible");
        REGULAR_RECIPE_KEY = new NamespacedKey(this, "invisible_item_frame");
        GLOW_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame");
        SHAPELESS_GLOW_RECIPE_KEY = new NamespacedKey(this, "invisible_glow_item_frame_shapeless");
    }

    @Override
    public void onEnable() {
        // register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ItemFramePlaceListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameBreakListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameInteractionListener(this, IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameCraftListener(IS_INVISIBLE_KEY), this);

        // load config and register recipes
        saveDefaultConfig();
        loadConfig(IS_INVISIBLE_KEY);

        // register shapeless glow item frame recipe
        registerShapelessGlowRecipe(SHAPELESS_GLOW_RECIPE_KEY);

        firstLoad = false;

        // incl metrics for bStats
        int pluginId = 25837;
        @SuppressWarnings("unused") Metrics metrics = new Metrics(this, pluginId);
    }

    private void addRecipeFromConfig(NamespacedKey key, ConfigurationSection config, ItemStack item) {
        item = item.clone();
        item.setAmount(config.getInt("count"));
        ShapedRecipe recipe = new ShapedRecipe(key, item);
        List<String> shape = config.getStringList("shape");
        recipe.shape(shape.toArray(new String[0]));

        ConfigurationSection ingredients = config.getConfigurationSection("ingredients");
        // If this is null, then the defaults above are incorrect.
        assert ingredients != null;
        for (Map.Entry<String, Object> entry : ingredients.getValues(false).entrySet()) {
            Material material = Material.matchMaterial(entry.getValue().toString());
            if (material == null) {
                getLogger()
                        .severe("Failed to find material " + entry.getValue().toString() + ", recipe might not work.");
                continue;
            }
            recipe.setIngredient(entry.getKey().charAt(0), material);
        }

        try {
            Bukkit.addRecipe(recipe);
        } catch (IllegalStateException ignored) {
            if (firstLoad) {
                getLogger().severe("Failed to add recipe " + config.getName() + ". This is likely an issue in the config");
            } else {
                getLogger().warning("Failed to add recipe " + config.getName() + ", because Spigot doesn't support reloading recipes.");
            }
        }
    }

    /**
     * Adds a shapeless recipe to allow crafting an invisible item frame
     * with a glow ink sac to make an invisible glow item frame.
     *
     * @param key plugin-scoped identifier for the recipe
     */
    private void registerShapelessGlowRecipe(NamespacedKey key) {
        ItemStack glowIs = ItemFrameRegistry.getInstance().getGlowInvisibleFrame();
        ItemStack regIs = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();

        ShapelessRecipe recipe = new ShapelessRecipe(key, glowIs.clone());
        recipe.addIngredient(Material.GLOW_INK_SAC);

        recipe.addIngredient(new RecipeChoice.ExactChoice(regIs.clone()));

        Bukkit.addRecipe(recipe);
    }

    /**
     * Holds structured data for an invisible item frame including name, lore, and enchantment glint status.
     *
     * @param name  The display name as a MiniMessage Component.
     * @param lore  The list of lore lines as MiniMessage Components.
     * @param glint Whether the item has the enchantment glint effect.
     */
    private record ItemFrameData(Component name, List<Component> lore, boolean glint) {
    }

    /**
     * Loads frame data from a given configuration section, deserializing text using MiniMessage.
     * The client code is expected to call `addConfigDefaults()` before this function.
     *
     * @param section The configuration section containing the item's data.
     * @return An {@link ItemFrameData} object containing the name, lore, and glint flag.
     */
    private ItemFrameData loadFrameData(ConfigurationSection section) {
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
     * Adds default configuration values for a specific invisible item frame type.
     *
     * @param config The plugin's configuration file.
     * @param id     The identifier for the item type (e.g., "invisible_item_frame").
     * @param type   The {@link Material} for the frame (e.g. Material.ITEM_FRAME).
     */
    private void addConfigDefaults(FileConfiguration config, String id, Material type) {
        config.addDefault("items." + id + ".name", "Invisible " + Utils.toTitleCase(type.name().replace("_", " ")));
        config.addDefault("recipes." + id + ".count", 8);
        config.addDefault("recipes." + id + ".glint", true);
        config.addDefault("recipes." + id + ".shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.addDefault("recipes." + id + ".ingredients.F", "minecraft:" + type.name());
        config.addDefault("recipes." + id + ".ingredients.A", "minecraft:phantom_membrane");
    }


    /**
     * Loads configuration data for invisible item frames, registers them with the item frame registry, and registers
     * their crafting recipes with the server.
     *
     * @param IS_INVISIBLE_KEY The key used for identifying invisible item frames.
     */
    public void loadConfig(NamespacedKey IS_INVISIBLE_KEY) {
        final FileConfiguration config = getConfig();

        // add default config values for both item frame types
        addConfigDefaults(config, "invisible_item_frame", Material.ITEM_FRAME);
        addConfigDefaults(config, "invisible_glow_item_frame", Material.GLOW_ITEM_FRAME);

        // load and register regular invisible item frame
        ConfigurationSection regularItem = config.getConfigurationSection("items.invisible_item_frame");
        assert regularItem != null;
        ItemFrameData regData = loadFrameData(regularItem);

        // register frame with registry
        ItemFrameRegistry.getInstance().registerRegularInvisibleItemFrame(
                IS_INVISIBLE_KEY, regData.name(), regData.lore(), regData.glint()
        );

        // get and register the crafting recipe
        ConfigurationSection regularRecipe = config.getConfigurationSection("recipes.invisible_item_frame");
        assert regularRecipe != null;
        addRecipeFromConfig(REGULAR_RECIPE_KEY, regularRecipe, ItemFrameRegistry.getInstance().getRegularInvisibleFrame());

        // load and register glow invisible item frame
        ConfigurationSection glowItem = config.getConfigurationSection("items.invisible_glow_item_frame");
        assert glowItem != null;
        ItemFrameData glowData = loadFrameData(glowItem);

        // register frame with registry
        ItemFrameRegistry.getInstance().registerGlowInvisibleItemFrame(
                IS_INVISIBLE_KEY, glowData.name(), glowData.lore(), glowData.glint()
        );

        // get and register the crafting recipe
        ConfigurationSection glowRecipe = config.getConfigurationSection("recipes.invisible_glow_item_frame");
        assert glowRecipe != null;
        addRecipeFromConfig(GLOW_RECIPE_KEY, glowRecipe, ItemFrameRegistry.getInstance().getGlowInvisibleFrame());
    }

}
