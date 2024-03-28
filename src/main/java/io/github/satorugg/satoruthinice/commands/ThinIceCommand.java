package io.github.satorugg.satoruthinice.commands;

import io.github.satorugg.satoruthinice.SatoruThinIce;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ThinIceCommand implements CommandExecutor {
    private final SetArenaCommand setArenaCommand;
    private SatoruThinIce plugin;

    public ThinIceCommand(SatoruThinIce plugin) {
        this.plugin = plugin;
        this.setArenaCommand = new SetArenaCommand(plugin);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player to execute this message");
            return false;
        }

        if (args.length == 0) {
            commandSender.sendMessage("Usage: /sthinice <set-arena | >");
            return true;
        }

        String subcommand = args[0];

        switch (subcommand.toLowerCase()) {
            case "set-arena":
                return setArenaCommand.onCommand(commandSender, command, s, Arrays.copyOfRange(args, 1, args.length));
            default:
                commandSender.sendMessage("Unknown subcommand!");
                return false;
        }
    }
}
