package com.atlasgong.invisibleitemframeslite;

import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameBreakListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameCraftListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFrameInteractionListener;
import com.atlasgong.invisibleitemframeslite.listeners.ItemFramePlaceListener;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

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
        firstLoad = false;

        // register listeners
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new ItemFramePlaceListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameBreakListener(IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameInteractionListener(this, IS_INVISIBLE_KEY), this);
        pm.registerEvents(new ItemFrameCraftListener(IS_INVISIBLE_KEY), this);

        // load config
        saveDefaultConfig();
        ConfigurationManager cm = new ConfigurationManager(getLogger(), getConfig());
        cm.addConfigDefaults("invisible_item_frame", Material.ITEM_FRAME);
        cm.addConfigDefaults("invisible_glow_item_frame", Material.GLOW_ITEM_FRAME);

        // register item frames with the registry
        ItemFrameRegistry registry = ItemFrameRegistry.getInstance();
        ItemFrameData regData = cm.loadItemData("invisible_item_frame");
        ItemFrameData glowData = cm.loadItemData("invisible_glow_item_frame");
        registry.registerRegularInvisibleItemFrame(getInvisibleKey(), regData.name(),
                regData.lore(), regData.glint());
        registry.registerGlowInvisibleItemFrame(getInvisibleKey(), glowData.name(),
                glowData.lore(), glowData.glint());

        // register recipes
        Recipe regRecipe = cm.loadRecipe(getRegularRecipeKey(), "invisible_item_frame",
                registry.getRegularInvisibleFrame());
        registerRecipe(regRecipe);

        Recipe glowRecipe = cm.loadRecipe(getGlowRecipeKey(), "invisible_glow_item_frame",
                registry.getGlowInvisibleFrame());
        registerRecipe(glowRecipe);

        // register shapeless glow item frame recipe
        registerShapelessGlowRecipe(getShapelessGlowRecipeKey());

        // incl metrics for bStats
        int pluginId = 25837;
        new Metrics(this, pluginId);
    }

    private void registerRecipe(Recipe recipe) {
        try {
            Bukkit.addRecipe(recipe);
        } catch (IllegalStateException ignored) {
            if (firstLoad) {
                getLogger().severe("Failed to add recipe. There is likely something wrong with the config. Try " +
                        "deleting the config and restarting the server to regenerate the default config.");
            } else {
                getLogger().warning("A plugin reload was detected. Plugin reloads are not supported. Crafting recipes" +
                        " failed to reload.");
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


}
