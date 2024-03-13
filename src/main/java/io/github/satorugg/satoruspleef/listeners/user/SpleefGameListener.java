package io.github.satorugg.satoruspleef.listeners.user;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class SpleefGameListener implements Listener {

    @EventHandler
    public void playerStepOnSpleefBlock(PlayerMoveEvent e) {
        if (e.getPlayer().getWorld().getBlockAt(e.getPlayer().getLocation()).hasMetadata("arena-block")) {
            System.out.println("player is on arena block");
        }
    }
}
