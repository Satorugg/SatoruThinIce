package io.github.satorugg.satoruspleef.game;

import io.github.satorugg.satoruspleef.SatoruSpleef;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class Arena {

    private List<Block> arenaBlocks = new ArrayList<>();
    private Block startingBlock;
    private Block endingBlock;
    private World world;

    public Arena(Block startingBlock, Block endingBlock, World world, Plugin plugin) {
        this.startingBlock = startingBlock;
        this.endingBlock = endingBlock;
        this.world = world;
        System.out.println("Arena has" + startingBlock);
        System.out.println("Arena has " + endingBlock);
        int maxX = Math.max(startingBlock.getX(), endingBlock.getX());
        int minX = Math.min(startingBlock.getX(), endingBlock.getX());
        System.out.printf("max x:%d min x:%d", maxX, minX);

        int maxY = Math.max(startingBlock.getY(), endingBlock.getY());
        int minY = Math.min(startingBlock.getY(), endingBlock.getY());
        System.out.printf("max y:%d min y:%d", maxY, minY);

        int maxZ = Math.max(startingBlock.getZ(), endingBlock.getZ());
        int minZ = Math.min(startingBlock.getZ(), endingBlock.getZ());
        System.out.printf("max z:%d min z:%d", maxZ, minZ);
        for (int i = minX; i <= maxX; i++) {
            System.out.println("looping up i " + i);
            for (int j = minY; j <= maxY; j++) {
                System.out.println("looping up j " + j);
                for (int k = minZ; k <= maxZ; k++) {
                    System.out.println("looping up k " + k);
                    if (world.getBlockAt(i, j, k).getType() == Material.AIR) {
                        System.out.println("not adding air");
                    } else {
                        Block b = world.getBlockAt(i, j, k);
                        b.setMetadata("arena-block", new FixedMetadataValue(plugin, "arenaBlock"));
                        arenaBlocks.add(b);
                    }
                }
            }
        }
        System.out.println(arenaBlocks.size());
        System.out.println(arenaBlocks);
    }

    public List<Block> getArenaBlocks() {
        return arenaBlocks;
    }
}
