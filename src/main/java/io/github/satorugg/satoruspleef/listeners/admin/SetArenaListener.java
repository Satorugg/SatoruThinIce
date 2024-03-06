package io.github.satorugg.satoruspleef.listeners.admin;

import io.github.satorugg.satoruspleef.game.Arena;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SetArenaListener implements Listener {
    HashMap<UUID, List<Block>> opArenaPointsMap = new HashMap<>();

    @EventHandler
    public boolean setArena(BlockBreakEvent e) {
        if (!e.getPlayer().isOp()) {
            return false;
        }
        e.setCancelled(true);

        UUID opPlayer = e.getPlayer().getUniqueId();
        if (!opArenaPointsMap.containsKey(opPlayer)) {
            System.out.println("Placing player");
            opArenaPointsMap.put(opPlayer, new ArrayList<>());
        }
        opArenaPointsMap.get(opPlayer).add(e.getBlock());
        if (opArenaPointsMap.get(opPlayer).size() >= 2) {
            System.out.println("Player list full");
            System.out.println(opArenaPointsMap.get(opPlayer).get(0));
            Block starting = opArenaPointsMap.get(opPlayer).get(0);
            Block ending = opArenaPointsMap.get(opPlayer).get(1);
            Arena arena = new Arena(starting, ending, e.getPlayer().getWorld());
            System.out.println(arena.getArenaBlocks().get(0));
            System.out.println(arena.getArenaBlocks().get(arena.getArenaBlocks().size() - 1));
            opArenaPointsMap.remove(opPlayer);
            return true;
        }
        System.out.println("Set arena Listener");
        System.out.println(opArenaPointsMap.get(opPlayer).size());
        return true;
    }
}
