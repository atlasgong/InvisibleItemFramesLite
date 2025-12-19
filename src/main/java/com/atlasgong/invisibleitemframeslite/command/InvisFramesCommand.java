package com.atlasgong.invisibleitemframeslite.command;

import com.atlasgong.invisibleitemframeslite.ItemFrameRegistry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the /invisframes command.
 * <p>
 * This command allows players with the appropriate permission to receive
 * invisible item frames via subcommands.
 *
 * <pre>
 * Usage:
 *   /invisframes give regular
 *   /invisframes give glow
 * </pre>
 * <p>
 * Tab-completion is provided for the "give" subcommand and its arguments.
 */
public class InvisFramesCommand implements CommandExecutor, TabCompleter {

    /**
     * Executes the /invisframes command.
     *
     * @param sender  the command sender
     * @param command the command being executed
     * @param label   the alias used to execute the command
     * @param args    command arguments
     * @return true if the command was handled
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender,
                             @NotNull Command command,
                             @NotNull String label,
                             String[] args) {

        // Ensure the command is executed by a player
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cThis command can only be used by players.");
            return true;
        }

        // Handle "give" subcommand
        if (args.length >= 2 && args[0].equalsIgnoreCase("give")) {
            // Permission check
            if (!player.hasPermission("invisibleitemframeslite.give")) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }

            give(player, args[1]);
        } else {
            help(player);
        }
        return true;
    }

    /**
     * Gives an invisible item frame to the player based on the specified type.
     *
     * @param player the player receiving the item frame
     * @param type   the frame type ("regular" or "glow")
     */
    private void give(Player player, String type) {
        ItemFrameRegistry registry = ItemFrameRegistry.getInstance();
        ItemStack frame;

        switch (type.toLowerCase()) {
            case "regular" -> frame = registry.getRegularInvisibleFrame();
            case "glow" -> frame = registry.getGlowInvisibleFrame();
            default -> {
                help(player);
                return;
            }
        }

        player.getInventory().addItem(frame);
    }

    /**
     * Sends the command usage message to the player.
     *
     * @param player the player to send the help message to
     */
    private void help(Player player) {
        player.sendMessage("§cUsage: /invisframes give <regular|glow>");
    }

    /**
     * Provides tab-completion for the /invisframes command.
     *
     * @param sender  the command sender
     * @param command the command being tab-completed
     * @param alias   the alias used
     * @param args    current command arguments
     * @return a list of possible completions
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args) {

        List<String> list = new ArrayList<>();

        if (sender instanceof Player player) {
            if (args.length == 1) {
                if (player.hasPermission("invisibleitemframeslite.command.give")) {
                    list.add("give");
                }
            } else if (args.length == 2 && args[0].equalsIgnoreCase("give") &&
                    player.hasPermission("invisibleitemframeslite.command.give")) {
                list = List.of("regular", "glow");
            }
        }
        return list;
    }
}
