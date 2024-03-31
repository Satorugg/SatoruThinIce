package io.github.satorugg.satoruthinice.game;

import io.github.satorugg.satoruthinice.SatoruThinIce;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Arena {

    private List<Block> arenaBlocks = new ArrayList<>();
    private Block startingBlock;
    private Block endingBlock;
    private World world;
    private SatoruThinIce plugin;
    private int arenaID;

    public Arena(int arenaID, Block startingBlock, Block endingBlock, World world, SatoruThinIce plugin) {
        this.startingBlock = startingBlock;
        this.endingBlock = endingBlock;
        this.world = world;
        this.plugin = plugin;
        this.arenaID = arenaID;
        int maxX = Math.max(this.startingBlock.getX(), this.endingBlock.getX());
        int minX = Math.min(this.startingBlock.getX(), this.endingBlock.getX());

        int maxY = Math.max(this.startingBlock.getY(), this.endingBlock.getY());
        int minY = Math.min(this.startingBlock.getY(), this.endingBlock.getY());

        int maxZ = Math.max(this.startingBlock.getZ(), this.endingBlock.getZ());
        int minZ = Math.min(this.startingBlock.getZ(), this.endingBlock.getZ());
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                for (int k = minZ; k <= maxZ; k++) {
                    if (this.world.getBlockAt(i, j, k).getType() != Material.AIR) {
                        Block b = this.world.getBlockAt(i, j, k);
                        //plugin.getDataSource().
                        b.setMetadata("ArenaBlock", new FixedMetadataValue(this.plugin, true));
                        arenaBlocks.add(b);
                    }
                }
            }
        }
        try (Connection connection = this.plugin.getDataSource().getConnection()) {
            String getArenaQuery = "INSERT INTO Blocks (ArenaID, x, y, z, block_material) VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(getArenaQuery)) {
                for (int i = 0; i < arenaBlocks.size(); i++) {
                    int j = 1;
                    Block b = arenaBlocks.get(i);
                    preparedStatement.setInt(j, this.arenaID);
                    j++;
                    preparedStatement.setInt(j, b.getX());
                    j++;
                    preparedStatement.setInt(j, b.getY());
                    j++;
                    preparedStatement.setInt(j, b.getZ());
                    j++;
                    preparedStatement.setString(j, b.getType().toString());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        System.out.println(arenaBlocks.size());
    }

    public List<Block> getArenaBlocks() {
        return arenaBlocks;
    }
}
