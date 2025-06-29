package com.atlasgong.invisibleitemframeslite;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ItemFrameRegistryTest extends MockBukkitTest {

    private NamespacedKey isInvisibleKey;
    private ItemFrameRegistry registry;

    @BeforeEach
    void setUp() {
        MockBukkit.mock();
        InvisibleItemFramesLite plugin = MockBukkit.load(InvisibleItemFramesLite.class);
        isInvisibleKey = plugin.getInvisibleKey();
        registry = ItemFrameRegistry.getInstance();
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

    @Test
    void testGetInstance_notNull() {
        ItemFrameRegistry instance = ItemFrameRegistry.getInstance();
        assertNotNull(instance, "getInstance() should never return null");
    }

    @Test
    void testGetInstance_returnsSameInstance() {
        ItemFrameRegistry instance1 = ItemFrameRegistry.getInstance();
        ItemFrameRegistry instance2 = ItemFrameRegistry.getInstance();

        assertSame(instance1, instance2, "getInstance() should return the same instance");
    }

    @Test
    void testCreate_regularItemFrame() {
        Component name = Component.text("Test Invisible Frame").color(NamedTextColor.WHITE);
        List<Component> lore = Arrays.asList(
                Component.text("line 1").color(NamedTextColor.GRAY),
                Component.text("line 2").color(NamedTextColor.GRAY)
        );
        boolean enchantmentGlint = true;
        boolean glow = false;

        ItemStack result = registry.create(isInvisibleKey, name, lore, enchantmentGlint, glow);

        assertNotNull(result, "created item should not be null");
        assertEquals(Material.ITEM_FRAME, result.getType(), "should create regular item frame");
        assertEquals(1, result.getAmount(), "amount should be 1");

        ItemMeta meta = result.getItemMeta();
        assertNotNull(meta, "ItemMeta should not be null");
        assertEquals(name, meta.customName(), "custom name should match");
        assertEquals(lore, meta.lore(), "lore should match");
        assertTrue(meta.getPersistentDataContainer().has(isInvisibleKey, PersistentDataType.BYTE),
                "should have invisible marker");
        assertEquals((byte) 1, meta.getPersistentDataContainer().get(isInvisibleKey, PersistentDataType.BYTE),
                "invisible marker should be set to 1");
        assertEquals(enchantmentGlint, meta.getEnchantmentGlintOverride(), "enchantment glint should match");
    }

    @Test
    void testCreate_glowItemFrame() {
        Component name = Component.text("Test Invisible Glow Frame").color(NamedTextColor.YELLOW);
        List<Component> lore = Collections.singletonList(Component.text("glow test").color(NamedTextColor.GOLD));
        boolean enchantmentGlint = false;
        boolean glow = true;

        ItemStack result = registry.create(isInvisibleKey, name, lore, enchantmentGlint, glow);

        assertNotNull(result, "created item should not be null");
        assertEquals(Material.GLOW_ITEM_FRAME, result.getType(), "should create glow item frame");
        assertEquals(1, result.getAmount(), "amount should be 1");

        ItemMeta meta = result.getItemMeta();
        assertNotNull(meta, "ItemMeta should not be null");
        assertEquals(name, meta.customName(), "custom name should match");
        assertEquals(lore, meta.lore(), "lore should match");
        assertTrue(meta.getPersistentDataContainer().has(isInvisibleKey, PersistentDataType.BYTE),
                "should have invisible marker");
        assertEquals((byte) 1, meta.getPersistentDataContainer().get(isInvisibleKey, PersistentDataType.BYTE),
                "invisible marker should be set to 1");
        assertEquals(enchantmentGlint, meta.getEnchantmentGlintOverride(), "enchantment glint should match");
    }

    @Test
    void testCreate_withNullLore() {
        Component name = Component.text("Test Frame");

        ItemStack result = registry.create(isInvisibleKey, name, null, false, false);

        ItemMeta meta = result.getItemMeta();
        assertNotNull(meta, "ItemMeta should not be null");
        assertNull(meta.lore(), "null lore should be preserved");
    }

    @Test
    void testRegisterRegularInvisibleItemFrame() {
        Component name = Component.text("Custom Regular Frame").color(NamedTextColor.BLUE);
        List<Component> lore = Arrays.asList(
                Component.text("Custom").color(NamedTextColor.AQUA),
                Component.text("Lore").color(NamedTextColor.AQUA)
        );
        boolean enchantmentGlint = false;

        registry.registerRegularInvisibleItemFrame(isInvisibleKey, name, lore, enchantmentGlint);

        ItemStack regularFrame = registry.getRegularInvisibleFrame();
        assertNotNull(regularFrame, "regular frame should be registered");
        assertEquals(Material.ITEM_FRAME, regularFrame.getType(), "should be regular item frame");

        ItemMeta meta = regularFrame.getItemMeta();
        assertNotNull(meta, "ItemMeta should not be null");
        assertEquals(name, meta.customName(), "custom name should match registered value");
        assertEquals(lore, meta.lore(), "lore should match registered value");
        assertEquals(enchantmentGlint, meta.getEnchantmentGlintOverride(), "enchantment glint should match");
    }

    @Test
    void testRegisterGlowInvisibleItemFrame() {
        Component name = Component.text("Custom Glow Frame").color(NamedTextColor.GREEN);
        List<Component> lore = Arrays.asList(
                Component.text("glow").color(NamedTextColor.LIGHT_PURPLE),
                Component.text("custom").color(NamedTextColor.LIGHT_PURPLE)
        );
        boolean enchantmentGlint = false;

        registry.registerGlowInvisibleItemFrame(isInvisibleKey, name, lore, enchantmentGlint);

        ItemStack glowFrame = registry.getGlowInvisibleFrame();
        assertNotNull(glowFrame, "glow frame should be registered");
        assertEquals(Material.GLOW_ITEM_FRAME, glowFrame.getType(), "should be glow item frame");

        ItemMeta meta = glowFrame.getItemMeta();
        assertNotNull(meta, "ItemMeta should not be null");
        assertEquals(name, meta.customName(), "custom name should match registered value");
        assertEquals(lore, meta.lore(), "lore should match registered value");
        assertEquals(enchantmentGlint, meta.getEnchantmentGlintOverride(), "enchantment glint should match");
    }

    @Test
    void testGetRegularInvisibleFrame_returnsClone() {
        // register a frame first
        Component name = Component.text("Test");
        registry.registerRegularInvisibleItemFrame(isInvisibleKey, name, Collections.emptyList(), false);

        ItemStack frame1 = registry.getRegularInvisibleFrame();
        ItemStack frame2 = registry.getRegularInvisibleFrame();

        assertNotNull(frame1, "first call should return item");
        assertNotNull(frame2, "second call should return item");
        assertNotSame(frame1, frame2, "should return different instances (clones)");
        assertEquals(frame1.getType(), frame2.getType(), "clones should have same type");
        assertNotSame(frame1.getItemMeta(), frame2.getItemMeta(), "clones should have different metadata instances");
        assertEquals(frame1.getItemMeta(), frame2.getItemMeta(), "clones should have equivalent metadata");
    }

    @Test
    void testGetGlowInvisibleFrame_returnsClone() {
        // register a frame first
        Component name = Component.text("Test Glow");
        registry.registerGlowInvisibleItemFrame(isInvisibleKey, name, Collections.emptyList(), false);

        ItemStack frame1 = registry.getGlowInvisibleFrame();
        ItemStack frame2 = registry.getGlowInvisibleFrame();

        assertNotNull(frame1, "first call should return item");
        assertNotNull(frame2, "second call should return item");
        assertNotSame(frame1, frame2, "should return different instances (clones)");
        assertEquals(frame1.getType(), frame2.getType(), "clones should have same type");
        assertNotSame(frame1.getItemMeta(), frame2.getItemMeta(), "clones should have different metadata instances");
        assertEquals(frame1.getItemMeta(), frame2.getItemMeta(), "clones should have equivalent metadata");
    }

    @Test
    void testBothFrameTypesCanBeRegisteredIndependently() {
        Component regularName = Component.text("Regular Frame").color(NamedTextColor.RED);
        Component glowName = Component.text("Glow Frame").color(NamedTextColor.YELLOW);
        List<Component> regularLore = Arrays.asList(
                Component.text("Regular").color(NamedTextColor.GRAY),
                Component.text("Lore").color(NamedTextColor.GRAY)
        );
        List<Component> glowLore = Arrays.asList(
                Component.text("Glow").color(NamedTextColor.GOLD),
                Component.text("Lore").color(NamedTextColor.GOLD)
        );

        registry.registerRegularInvisibleItemFrame(isInvisibleKey, regularName, regularLore, true);
        registry.registerGlowInvisibleItemFrame(isInvisibleKey, glowName, glowLore, false);

        ItemStack regularFrame = registry.getRegularInvisibleFrame();
        ItemStack glowFrame = registry.getGlowInvisibleFrame();

        // verify they are different
        assertNotEquals(regularFrame.getType(), glowFrame.getType(), "frame types should be different");
        assertNotEquals(regularFrame.getItemMeta().customName(),
                glowFrame.getItemMeta().customName(), "names should be different");
        assertNotEquals(regularFrame.getItemMeta().lore(),
                glowFrame.getItemMeta().lore(), "lore should be different");
        assertNotEquals(regularFrame.getItemMeta().getEnchantmentGlintOverride(),
                glowFrame.getItemMeta().getEnchantmentGlintOverride(), "glint settings should be different");
    }

    @Test
    void testCreate_withComplexComponents() {
        // test with more complex Adventure components
        Component name = Component.text()
                .append(Component.text("Invisible ").color(NamedTextColor.GRAY))
                .append(Component.text("Item Frame").color(NamedTextColor.WHITE))
                .build();

        List<Component> lore = Arrays.asList(
                Component.text("• ").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text("Special frame").color(NamedTextColor.AQUA)),
                Component.text("• ").color(NamedTextColor.DARK_GRAY)
                        .append(Component.text("Invisible when placed").color(NamedTextColor.GREEN))
        );

        ItemStack result = registry.create(isInvisibleKey, name, lore, true, false);

        assertNotNull(result, "created item should not be null");
        ItemMeta meta = result.getItemMeta();
        assertNotNull(meta, "ItemMeta should not be null");
        assertEquals(name, meta.customName(), "complex display name should be preserved");
        assertEquals(lore, meta.lore(), "complex lore should be preserved");
    }
}