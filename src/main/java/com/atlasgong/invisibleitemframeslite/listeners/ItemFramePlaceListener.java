package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.persistence.PersistentDataType;

/** Listener that handles placement of invisible item frames. */
public class ItemFramePlaceListener implements Listener {

    /** Key used to tag item frames as invisible via persistent data. */
    private final NamespacedKey isInvisibleKey;

    /**
     * Constructs a new ItemFramePlaceListener.
     *
     * @param isInvisibleKey The {@link NamespacedKey} used to identify invisible item frames.
     */
    public ItemFramePlaceListener(NamespacedKey isInvisibleKey) {
        this.isInvisibleKey = isInvisibleKey;
    }

    /**
     * Handles the event triggered when a hanging entity (such as an item frame) is placed.
     * If the player previously attempted to place an invisible item frame, the newly created
     * item frame entity is tagged as invisible using persistent data.
     */
    @EventHandler
    public void onHangingPlace(HangingPlaceEvent e) {
        if (Utils.isInvisibleItemFrame(e.getItemStack(), isInvisibleKey)) {
            e.getEntity().getPersistentDataContainer().set(isInvisibleKey,
                    PersistentDataType.BYTE, (byte) 1);
        }
    }

}
