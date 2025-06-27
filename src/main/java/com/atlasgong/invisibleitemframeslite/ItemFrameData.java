package com.atlasgong.invisibleitemframeslite;

import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * Holds structured data for an invisible item frame including name, lore, and enchantment glint status.
 *
 * @param name  The display name as a MiniMessage Component.
 * @param lore  The list of lore lines as MiniMessage Components.
 * @param glint Whether the item has the enchantment glint effect.
 */
public record ItemFrameData(Component name, List<Component> lore, boolean glint) {

}
