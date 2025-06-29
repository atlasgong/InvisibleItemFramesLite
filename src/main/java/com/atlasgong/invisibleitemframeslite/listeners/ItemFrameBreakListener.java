package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.ItemFrameRegistry;
import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.GlowItemFrame;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listener that ensures broken item frame items restore their {@link ItemMeta}
 * when they are broken/dropped for any {@link org.bukkit.event.hanging.HangingBreakEvent.RemoveCause}.
 */
public class ItemFrameBreakListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;

    /**
     * Constructs a new ItemFrameBreakListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFrameBreakListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Paper doesn't provide drop data in the {@link HangingBreakEvent} event. This is problematic because we aren't
     * able to set the metadata for the dropped item ({@link ItemMeta} does not transfer to its entity when its
     * corresponding {@link ItemStack} is placed), and thus, an invisible item frame would drop as a regular one. To
     * work around this, we cancel the event, manually remove the item entity, then manually drop the corresponding
     * {@link ItemStack}.
     */
    @EventHandler()
    public void onHangingBreak(HangingBreakEvent e) {
        final boolean isInvisFrame = Utils.isInvisibleItemFrame(e.getEntity(), isInvisibleKey);
        if (!isInvisFrame) return;

        final ItemFrame invisFrame = (ItemFrame) e.getEntity(); // safe to cast given check above
        final Location loc = invisFrame.getLocation();

        e.setCancelled(true);
        invisFrame.remove();

        // drop the item inside the frame if there was one
        if (!invisFrame.getItem().getType().isAir()) {
            dropItem(loc, invisFrame.getItem());
        }

        // simulate item frame dropping
        if (invisFrame instanceof GlowItemFrame) {
            ItemStack glowInvisItemFrame = ItemFrameRegistry.getInstance().getGlowInvisibleFrame();
            dropItem(loc, glowInvisItemFrame, Sound.ENTITY_GLOW_ITEM_FRAME_BREAK);
        } else { // regular item frame
            ItemStack regInvisItemFrame = ItemFrameRegistry.getInstance().getRegularInvisibleFrame();
            dropItem(loc, regInvisItemFrame, Sound.ENTITY_ITEM_FRAME_BREAK);
        }
    }

    /**
     * Simulates an item drop.
     *
     * @param loc        The location to naturally drop an item at.
     * @param itemToDrop The item to be dropped.
     * @param sound      The sound to be played.
     */
    private void dropItem(Location loc, ItemStack itemToDrop, Sound sound) {
        World w = loc.getWorld();
        w.dropItemNaturally(loc, itemToDrop);
        if (sound != null) w.playSound(loc, sound, SoundCategory.BLOCKS, 1.0f, 1.0f);
        // we do not need to adjust player stats, given the dropped item was from an entity and not a block
    }

    /**
     * Simulates an item drop.
     *
     * @param loc        The location to naturally drop an item at.
     * @param itemToDrop The item to be dropped.
     */
    private void dropItem(Location loc, ItemStack itemToDrop) {
        dropItem(loc, itemToDrop, null);
    }

}
