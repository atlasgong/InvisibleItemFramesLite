package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/** Listener that handles interactions with invisible item frames. */
public class ItemFrameInteractionListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;

    /**
     * Constructs a new ItemFrameInteractionListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFrameInteractionListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Toggles visibility of invisible item frames s.t. they are visible when empty and invisible when filled.
     * Allows "passing through" the item frame to open the container behind it, if the player is not sneaking.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onItemFrameChange(PlayerItemFrameChangeEvent e) {
        ItemFrame frame = e.getItemFrame();
        if (!Utils.isInvisibleItemFrame(frame, isInvisibleKey)) {
            return;
        }

        switch (e.getAction()) {
            case PLACE -> frame.setVisible(false);
            case REMOVE -> frame.setVisible(true);
            case ROTATE -> { // pass-through logic
                Player p = e.getPlayer();
                if (p.isSneaking()) return; // allow rotations when player sneaking

                BlockFace attachedFace = e.getItemFrame().getAttachedFace();
                Block mount = e.getItemFrame().getLocation().getBlock().getRelative(attachedFace);
                if (mount.getState() instanceof Container container) {
                    e.setCancelled(true);
                    p.openInventory(container.getInventory());
                }
            }
        }
    }

}
