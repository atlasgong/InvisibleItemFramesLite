package com.atlasgong.invisibleitemframeslite;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilsTest extends MockBukkitTest {

    private ServerMock server;
    private InvisibleItemFramesLite plugin;
    private NamespacedKey isInvisibleKey;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(InvisibleItemFramesLite.class);
        isInvisibleKey = plugin.getInvisibleKey();
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
    void testIsInvisibleItemFrame_withItemStack() {
        // test with native item frame
        ItemStack nativeFrame = new ItemStack(Material.ITEM_FRAME);
        assert !Utils.isInvisibleItemFrame(nativeFrame, isInvisibleKey);

        // test with native glow item frame
        ItemStack nativeGlowFrame = new ItemStack(Material.GLOW_ITEM_FRAME);
        assert !Utils.isInvisibleItemFrame(nativeGlowFrame, isInvisibleKey);

        // test with regular invis item frame
        ItemStack regInvisFrame = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();
        assert Utils.isInvisibleItemFrame(regInvisFrame, isInvisibleKey);

        // test with glow invis item frame
        ItemStack regGlowFrame = ItemFrameRegistry.getInstance().getGlowInvisibleFrame();
        assert Utils.isInvisibleItemFrame(regGlowFrame, isInvisibleKey);
    }

    @Test
    void testIsInvisibleItemFrame_withEntity() {
        World w = server.createWorld(new WorldCreator("world"));
        assert w != null;
        Location loc = new Location(w, 0, 64, 0);
        Block b = w.getBlockAt(loc);
        b.setType(Material.STONE);

        // create a regular item frame entity
        ItemFrame regularFrame = (ItemFrame) w.spawnEntity(loc, EntityType.ITEM_FRAME);
        assert !Utils.isInvisibleItemFrame(regularFrame, isInvisibleKey);

        // create an invisible item frame entity by adding the persistent data
        ItemFrame invisibleFrame = (ItemFrame) w.spawnEntity(loc.add(1, 0, 0), EntityType.ITEM_FRAME);
        invisibleFrame.getPersistentDataContainer().set(isInvisibleKey, org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
        assert Utils.isInvisibleItemFrame(invisibleFrame, isInvisibleKey);

        // test with non-ItemFrame entity
        org.bukkit.entity.Entity nonFrameEntity = w.spawnEntity(loc.add(2, 0, 0), EntityType.ZOMBIE);
        assert !Utils.isInvisibleItemFrame(nonFrameEntity, isInvisibleKey);
    }

    @Test
    void testToTitleCase_withExpectedString() {
        assert Utils.toTitleCase("invisible item frame").equals("Invisible Item Frame");
    }

    @Test
    void testToTitleCase_withUnexpectedString() {
        // this testcase shows that toTitleCase only capitalizes the first letter of each word
        assert Utils.toTitleCase("inviSible iTEM frAMe").equals("InviSible ITEM FrAMe");
    }

    @Test
    void testToTitleCase_withEmptyString() {
        // this testcase shows that toTitleCase is not error safe
        assertThrows(StringIndexOutOfBoundsException.class, () -> Utils.toTitleCase("").isEmpty());
    }

}