package com.atlasgong.invisibleitemframeslite.listeners;

import com.atlasgong.invisibleitemframeslite.Utils;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
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

    /** Toggles visibility of invisible item frames s.t. they are visible when empty and invisible when filled. */
    @EventHandler
    public void onItemFrameChange(PlayerItemFrameChangeEvent e) {
        ItemFrame frame = e.getItemFrame();
        if (!Utils.isInvisibleItemFrame(frame, isInvisibleKey)) {
            return;
        }

        switch (e.getAction()) {
            case PLACE -> frame.setVisible(false);
            case REMOVE -> frame.setVisible(true);
        }
    }

}
