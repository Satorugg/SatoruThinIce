package io.github.satorugg.satoruthinice.listeners.user;

import io.github.satorugg.satoruthinice.SatoruThinIce;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;

public class ThinIceGameListener implements Listener {

    private SatoruThinIce plugin;

    private HashMap<Integer, Block> touchedBlocksInArena = new HashMap<>();

    public ThinIceGameListener(SatoruThinIce plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerStepOnThinIceBlock(PlayerMoveEvent e) {
        System.out.println("player not on arena block");
        Location playerLocation = e.getPlayer().getLocation();
        Block b = playerLocation.getBlock().getRelative(BlockFace.DOWN);
        int blockArenaNum = -1; // add this functionality later, get arena number and dont attach
                                // run scheduler on already touched blocks.
        if (isArenaBlock(b)) {
            Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
                @Override
                public void run() {
                    System.out.println("running on block " + b);
                    e.getPlayer().getWorld().getBlockAt(b.getLocation()).setType(Material.AIR);
                }
            }, 20L);
        }
    }

    public boolean isArenaBlock(Block b) {
        return b.hasMetadata("ArenaBlock");
    }
}
