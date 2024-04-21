package io.github.satorugg.satoruthinice.game;

import io.github.satorugg.satoruthinice.SatoruThinIce;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Arena {

    private HashMap<Block, Material> arenaBlocks = new HashMap<>();
    private Block startingBlock;
    private Block endingBlock;
    private World world;
    private SatoruThinIce plugin;
    public int arenaID;

    public Arena(int arenaID, Block startingBlock, Block endingBlock, World world, SatoruThinIce plugin) {
        this.startingBlock = startingBlock;
        this.endingBlock = endingBlock;
        this.world = world;
        this.plugin = plugin;
        this.arenaID = arenaID;

        // Setting Arena for first time.
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
                        b.setMetadata("ArenaBlock", new FixedMetadataValue(this.plugin, true));
                        arenaBlocks.put(b, b.getType());
                    }
                }
            }
        }
        writeBlocksToDB();
    }

    public Arena(int arenaID, SatoruThinIce plugin, World world) {
        this.arenaID = arenaID;
        this.plugin = plugin;
        this.world = world;

        //writeBlocksToDB();
        //resetArena();
    }

    public void writeBlocksToDB() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // batch execution for arena blocks setting due to large amount of SQL transactions.
                try (Connection connection = plugin.getDataSource().getConnection()) {
                    String getArenaQuery = "INSERT INTO Blocks (ArenaID, x, y, z, block_material, world) VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(getArenaQuery)) {
                        connection.setAutoCommit(false); // Disable auto commit for batch execution
                        for (Block b : arenaBlocks.keySet()) {
                            preparedStatement.setInt(1, arenaID);
                            preparedStatement.setInt(2, b.getX());
                            preparedStatement.setInt(3, b.getY());
                            preparedStatement.setInt(4, b.getZ());
                            preparedStatement.setString(5, b.getType().toString());
                            preparedStatement.setString(6, b.getWorld().getName());
                            preparedStatement.addBatch(); // add insert operation to batch
                        }
                        preparedStatement.executeBatch(); // execute the batch of insert operations
                        connection.commit(); // commit the transaction
                    } catch (SQLException ex) {
                        connection.rollback(); // rollback in case of exception.
                        throw new RuntimeException(ex);
                    } finally {
                        connection.setAutoCommit(true); // restore auto commit mode.
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void resetArena() {
        System.out.println("reset arena");
        for (Block b : arenaBlocks.keySet()) {
            b.setType(arenaBlocks.get(b));
            if (b.getType() == Material.AIR) {
                System.out.println(b.getLocation());
            }
            if (b.getX() == -87 && b.getY() == 88 && b.getZ() == -170) {
                System.out.println(b.getType());
            }
            //b.setMetadata("ArenaBlock", new FixedMetadataValue(plugin, true));
        }
    }

    public void addArenaBlock(Block b, Material type) {
        arenaBlocks.put(b, type);
        b.setMetadata("ArenaBlock", new FixedMetadataValue(plugin, true));
    }
}
