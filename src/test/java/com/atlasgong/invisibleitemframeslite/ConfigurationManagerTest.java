package com.atlasgong.invisibleitemframeslite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigurationManagerTest extends MockBukkitTest {

    private InvisibleItemFramesLite plugin;
    private Logger logger;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        plugin = MockBukkit.load(InvisibleItemFramesLite.class);
        logger = plugin.getLogger();
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();

        // bypass singleton enforcement
        try {
            Field field = ItemFrameRegistry.class.getDeclaredField("instance");
            field.setAccessible(true);
            field.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @ParameterizedTest
    @ValueSource(strings = {"invisible_item_frame", "invisible_glow_item_frame"})
    void testAddConfigDefaults(String variant) {
        Material type = Material.valueOf(variant.replaceFirst("invisible_", "").toUpperCase());

        ConfigurationManager cm = new ConfigurationManager(logger, plugin.getConfig());
        cm.addConfigDefaults(variant, type);

        FileConfiguration config = plugin.getConfig();
        String itemName = config.getString("items." + variant + ".name");
        assert itemName != null;

        assertEquals(MiniMessage.miniMessage().deserialize("<!italic>" + Utils.toTitleCase(variant.replace('_', ' '))),
                MiniMessage.miniMessage().deserialize(itemName));
        assertTrue(config.getBoolean("items." + variant + ".enchantment_glint"));
        assertEquals(8, config.getInt("recipes." + variant + ".count"));
        assertEquals(Arrays.asList("FFF", "FAF", "FFF"), config.getStringList("recipes." + variant + ".shape"));
        assertEquals("minecraft:" + type.name().toLowerCase(), config.getString("recipes." + variant + ".ingredients.F"));
        assertEquals("minecraft:phantom_membrane",
                config.getString("recipes." + variant + ".ingredients.A"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invisible_item_frame", "invisible_glow_item_frame"})
    void testLoadItemData(String itemId) {
        String name = Utils.toTitleCase(itemId.replace('_', ' '));

        FileConfiguration config = plugin.getConfig();
        config.set("items." + itemId + ".lore", Arrays.asList("<red>lorem ipsum</red>", "§llegacy ipsum")); // set lore

        List<Component> lore = new ArrayList<>();
        lore.add(MiniMessage.miniMessage().deserialize("<red>lorem ipsum</red>"));
        lore.add(MiniMessage.miniMessage().deserialize("<bold>legacy ipsum</bold>"));

        ItemFrameData ifd = new ItemFrameData(
                MiniMessage.miniMessage().deserialize(name),
                lore,
                true
        );

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        ItemFrameData actual = cm.loadItemData(itemId);

        assertEquals("<!italic>" + MiniMessage.miniMessage().serialize(ifd.name()),
                MiniMessage.miniMessage().serialize(actual.name()), "name should be the same");

        assertEquals(ifd.lore(), actual.lore(), "lore should be the same");

        assertEquals(ifd.glint(), actual.glint(), "glint should be the same");
    }

    @ParameterizedTest
    @ValueSource(strings = {"invisible_item_frame", "invisible_glow_item_frame"})
    void testLoadRecipe(String itemId) {
        Material type = Material.valueOf(itemId.replaceFirst("invisible_", "").toUpperCase());

        ConfigurationManager cm = new ConfigurationManager(logger, plugin.getConfig());
        cm.addConfigDefaults(itemId, type);

        NamespacedKey recipeKey = new NamespacedKey(plugin, itemId);
        ItemStack result = new ItemStack(type);

        Recipe recipe = cm.loadRecipe(recipeKey, itemId, result);

        assertNotNull(recipe);
        assertInstanceOf(ShapedRecipe.class, recipe);

        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
        assertEquals(8, shapedRecipe.getResult().getAmount());
        assertEquals(type, shapedRecipe.getResult().getType());
    }

    @Test
    void testLoadItemData_missingConfigurationSection() {
        ConfigurationManager cm = new ConfigurationManager(logger, plugin.getConfig());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cm.loadItemData("nonexistent_item"));

        assertEquals("Missing configuration section for 'items.nonexistent_item'.", exception.getMessage());
    }

    @Test
    void testLoadItemData_emptyLore() {
        FileConfiguration config = plugin.getConfig();
        config.set("items.test_item.name", "Test Item");
        config.set("items.test_item.lore", Collections.emptyList());
        config.set("items.test_item.enchantment_glint", false);

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        ItemFrameData result = cm.loadItemData("test_item");

        assertNotNull(result);
        assertEquals("Test Item", MiniMessage.miniMessage().serialize(result.name()));
        assertTrue(result.lore().isEmpty());
        assertFalse(result.glint());
    }

    @Test
    void testLoadItemData_mixedLegacyAndMiniMessage() {
        FileConfiguration config = plugin.getConfig();
        config.set("items.test_item.name", "§6Gold Name");
        config.set("items.test_item.lore", Arrays.asList("§clegacy red", "<green>minimsg green</green>"));
        config.set("items.test_item.enchantment_glint", true);

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        ItemFrameData result = cm.loadItemData("test_item");

        assertNotNull(result);
        assertEquals(2, result.lore().size());
        assertTrue(result.glint());
    }

    @Test
    void testLoadItemData_onlyMiniMessage() {
        FileConfiguration config = plugin.getConfig();
        config.set("items.test_item.name", "<bold><blue>minimsg name</blue></bold>");
        config.set("items.test_item.lore", Arrays.asList("<red>red line</red>", "<italic>italic line</italic>"));
        config.set("items.test_item.enchantment_glint", false);

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        ItemFrameData result = cm.loadItemData("test_item");

        assertNotNull(result);
        assertEquals("<bold><blue>minimsg name", MiniMessage.miniMessage().serialize(result.name()));
        assertEquals(2, result.lore().size());
        assertFalse(result.glint());
    }

    @Test
    void testLoadRecipe_missingConfigurationSection() {
        ConfigurationManager cm = new ConfigurationManager(logger, plugin.getConfig());
        NamespacedKey recipeKey = new NamespacedKey(plugin, "test_recipe");
        ItemStack result = new ItemStack(Material.ITEM_FRAME);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> cm.loadRecipe(recipeKey, "nonexistent_recipe", result));

        assertEquals("Missing configuration section for 'recipes.nonexistent_recipe'.", exception.getMessage());
    }

    @Test
    void testLoadRecipe_zeroCount() {
        FileConfiguration config = plugin.getConfig();
        config.set("recipes.test_recipe.count", 0);
        config.set("recipes.test_recipe.shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.set("recipes.test_recipe.ingredients.F", "minecraft:item_frame");
        config.set("recipes.test_recipe.ingredients.A", "minecraft:phantom_membrane");

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        NamespacedKey recipeKey = new NamespacedKey(plugin, "test_recipe");
        ItemStack result = new ItemStack(Material.ITEM_FRAME);

        Recipe recipe = cm.loadRecipe(recipeKey, "test_recipe", result);

        assertNotNull(recipe);
        assertInstanceOf(ShapedRecipe.class, recipe);
        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
        assertEquals(1, shapedRecipe.getResult().getAmount()); // should fallback to 1
    }

    @Test
    void testLoadRecipe_invalidMaterial() {
        FileConfiguration config = plugin.getConfig();
        config.set("recipes.test_recipe.count", 4);
        config.set("recipes.test_recipe.shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.set("recipes.test_recipe.ingredients.F", "minecraft:invalid_material");
        config.set("recipes.test_recipe.ingredients.A", "minecraft:phantom_membrane");

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        NamespacedKey recipeKey = new NamespacedKey(plugin, "test_recipe");
        ItemStack result = new ItemStack(Material.ITEM_FRAME);

        Recipe recipe = cm.loadRecipe(recipeKey, "test_recipe", result);

        assertNotNull(recipe);
        assertInstanceOf(ShapedRecipe.class, recipe);
        // recipe should still be created, but invalid material should be skipped
    }

    @Test
    void testLoadRecipe_customCount() {
        FileConfiguration config = plugin.getConfig();
        config.set("recipes.test_recipe.count", 16);
        config.set("recipes.test_recipe.shape", Arrays.asList("FFF", "FAF", "FFF"));
        config.set("recipes.test_recipe.ingredients.F", "minecraft:item_frame");
        config.set("recipes.test_recipe.ingredients.A", "minecraft:phantom_membrane");

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        NamespacedKey recipeKey = new NamespacedKey(plugin, "test_recipe");
        ItemStack result = new ItemStack(Material.ITEM_FRAME);

        Recipe recipe = cm.loadRecipe(recipeKey, "test_recipe", result);

        assertNotNull(recipe);
        assertInstanceOf(ShapedRecipe.class, recipe);
        ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
        assertEquals(16, shapedRecipe.getResult().getAmount());
    }

    @Test
    void testAddConfigDefaults_overwriteExisting() {
        FileConfiguration config = plugin.getConfig();
        config.set("items.test_item.name", "Existing Name");
        config.set("items.test_item.enchantment_glint", false);

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        cm.addConfigDefaults("test_item", Material.ITEM_FRAME);

        // addDefault should not overwrite existing values
        assertEquals("Existing Name", config.getString("items.test_item.name"));
        assertFalse(config.getBoolean("items.test_item.enchantment_glint"));

        // but should add missing defaults
        assertEquals(8, config.getInt("recipes.test_item.count"));
    }

    @Test
    void testLoadRecipe_emptyShape() {
        FileConfiguration config = plugin.getConfig();
        config.set("recipes.test_recipe.count", 1);
        config.set("recipes.test_recipe.shape", Collections.emptyList());
        config.set("recipes.test_recipe.ingredients.F", "minecraft:item_frame");

        ConfigurationManager cm = new ConfigurationManager(logger, config);
        NamespacedKey recipeKey = new NamespacedKey(plugin, "test_recipe");
        ItemStack result = new ItemStack(Material.ITEM_FRAME);

        Recipe recipe = cm.loadRecipe(recipeKey, "test_recipe", result);

        assertNull(recipe);
    }
}
