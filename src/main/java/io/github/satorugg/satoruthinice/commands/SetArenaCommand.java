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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player in game to use this command!");
            return false;
        }
        System.out.println("running");
        if (args.length == 0) {
            commandSender.sendMessage("Please enter the arena number!");
            return true;
        }

        Player player = (Player) commandSender;
        ItemStack setArenaAxe = new ItemStack(Material.WOODEN_AXE);
        ItemMeta itemMeta = setArenaAxe.getItemMeta();
        assert itemMeta != null;
        String axeName = "Arena " + args[0] + " Axe";
        System.out.println("arg0" + args[0]);
        itemMeta.setDisplayName(axeName);
        itemMeta.setUnbreakable(true);
        setArenaAxe.setItemMeta(itemMeta);
        player.getInventory().addItem(setArenaAxe);
        return true;
    }
}
