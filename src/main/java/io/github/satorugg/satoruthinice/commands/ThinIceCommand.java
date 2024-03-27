package io.github.satorugg.satoruthinice.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class ThinIceCommand implements CommandExecutor {
    private final SetArenaCommand setArenaCommand;

    public ThinIceCommand() {
        this.setArenaCommand = new SetArenaCommand();
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Must be a player to execute this message");
            return false;
        }

        if (strings.length == 0) {
            commandSender.sendMessage("Usage: /sspleef <set-arena | >");
            return true;
        }

        String subcommand = strings[0];

        switch (subcommand.toLowerCase()) {
            case "set-arena":
                return setArenaCommand.onCommand(commandSender, command, s, Arrays.copyOfRange(strings, 1, strings.length));
            default:
                commandSender.sendMessage("Unknown subcommand!");
                return false;
        }
    }
}
