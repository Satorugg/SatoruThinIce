package io.github.satorugg.satoruthinice.commands;

import io.github.satorugg.satoruthinice.SatoruThinIce;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SetArenaCommand implements CommandExecutor {

    SatoruThinIce plugin;

    public SetArenaCommand(SatoruThinIce plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player in game to use this command!");
            return false;
        }
        if (args.length == 0) {
            commandSender.sendMessage("Please enter the arena number!");
            return true;
        }

        try (Connection connection = plugin.getDataSource().getConnection()) {
            String getArenaQuery = "SELECT * FROM Arenas AS a WHERE a.ArenaID = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(getArenaQuery)) {
                preparedStatement.setInt(1, Integer.parseInt(args[0]));
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int arenaID = resultSet.getInt("ArenaID");// Retrieve data by column name
                    // Process each row. For example, you can print the ArenaID
                    if (arenaID == Integer.parseInt(args[0])) {
                        commandSender.sendMessage("Arena already exists!");
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        Player player = (Player) commandSender;
        ItemStack setArenaAxe = new ItemStack(Material.WOODEN_AXE);
        ItemMeta itemMeta = setArenaAxe.getItemMeta();
        assert itemMeta != null;
        String axeName = "Arena " + args[0] + " Axe";
        itemMeta.setDisplayName(axeName);
        itemMeta.setUnbreakable(true);
        setArenaAxe.setItemMeta(itemMeta);
        player.getInventory().addItem(setArenaAxe);
        return true;
    }
}
