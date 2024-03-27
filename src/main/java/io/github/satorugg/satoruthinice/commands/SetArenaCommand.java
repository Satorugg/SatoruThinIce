package io.github.satorugg.satoruthinice.commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SetArenaCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player in game to use this command!");
            return false;
        }
        System.out.println("running");

        Player player = (Player) commandSender;
        ItemStack setArenaAxe = new ItemStack(Material.WOODEN_AXE);
        ItemMeta itemMeta = setArenaAxe.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName("Arena Axe");
        itemMeta.setUnbreakable(true);
        setArenaAxe.setItemMeta(itemMeta);
        player.getInventory().addItem(setArenaAxe);
        return true;
    }
}
