package io.github.satorugg.satoruthinice.listeners.user;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class ThinIceGameListener implements Listener {

    @EventHandler
    public void playerStepOnThinIceBlock(PlayerMoveEvent e) {
        Location playerLocation = e.getPlayer().getLocation();
        Block b = playerLocation.getBlock().getRelative(BlockFace.DOWN);
    }

    public boolean isArenaBlock(Block b) {
        return b.hasMetadata("ArenaBlock");
    }
}
