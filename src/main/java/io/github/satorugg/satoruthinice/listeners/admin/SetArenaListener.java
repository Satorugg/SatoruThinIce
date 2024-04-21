package io.github.satorugg.satoruthinice.listeners.admin;

import io.github.satorugg.satoruthinice.SatoruThinIce;
import io.github.satorugg.satoruthinice.game.Arena;
import io.github.satorugg.satoruthinice.game.ArenaManager;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetArenaListener implements Listener {
    HashMap<UUID, List<Block>> opArenaPointsMap = new HashMap<>();
    SatoruThinIce plugin;
    ArenaManager manager;

    public SetArenaListener(SatoruThinIce plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public boolean setArena(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            return false;
        }
        if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta() == null) {
            return false;
        }

        String playerHandItem = e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if (!playerHandItem.contains("Arena") && !playerHandItem.contains("Axe")) {
            return false;
        }
        e.setCancelled(true);

        UUID opPlayer = e.getPlayer().getUniqueId();
        if (!opArenaPointsMap.containsKey(opPlayer)) {
            opArenaPointsMap.put(opPlayer, new ArrayList<>());
        }
        opArenaPointsMap.get(opPlayer).add(e.getBlock());
        if (opArenaPointsMap.get(opPlayer).size() >= 2) {
            // This regex matches any sequence of digits (\d+)
            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(playerHandItem);
            int arenaID = -1;

            if (matcher.find()) {
                // matcher.group() will return the first sequence of digits found
                arenaID = Integer.parseInt(matcher.group());
            } else {
                System.out.println("No number found in the input string.");
            }

            try (Connection connection = plugin.getDataSource().getConnection()) {
                String insertArenaQuery = "INSERT INTO Arenas (ArenaID) VALUES(?)";
                try (PreparedStatement preparedStatement = connection.prepareStatement(insertArenaQuery)) {
                    preparedStatement.setInt(1, arenaID);
                    preparedStatement.executeUpdate();
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            e.getPlayer().sendMessage("Setting arena, please wait...");
            Block starting = opArenaPointsMap.get(opPlayer).get(0);
            Block ending = opArenaPointsMap.get(opPlayer).get(1);
            Arena arena = new Arena(arenaID, starting, ending, e.getPlayer().getWorld(), plugin);
            plugin.getArenaManager().addArena(arena);

            ItemStack i = e.getPlayer().getInventory().getItemInMainHand();
            e.getPlayer().getInventory().remove(i);
            opArenaPointsMap.remove(opPlayer);
            e.getPlayer().sendMessage("Arena set!");
            return true;
        }
        return true;
    }
}
