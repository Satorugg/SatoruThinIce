package io.github.satorugg.satoruthinice;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.satorugg.satoruthinice.commands.ThinIceCommand;
import io.github.satorugg.satoruthinice.game.Arena;
import io.github.satorugg.satoruthinice.game.ArenaManager;
import io.github.satorugg.satoruthinice.listeners.admin.SetArenaListener;
import io.github.satorugg.satoruthinice.listeners.user.ThinIceGameListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.checkerframework.checker.units.qual.A;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class SatoruThinIce extends JavaPlugin {

    private MysqlConnectionPoolDataSource dataSource;
    private Database db;
    private ArenaManager arenaManager;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
        FileConfiguration config = getConfig();
        this.arenaManager = new ArenaManager();

        // DB setup
        db = new Database(config);
        try {
            dataSource = initMySQLDataSource(db);
            initDb();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

        // Commands
        getCommand("sthinice").setExecutor(new ThinIceCommand(this));

        // Listeners
        getServer().getPluginManager().registerEvents(new SetArenaListener(this), this);
        getServer().getPluginManager().registerEvents(new ThinIceGameListener(this), this);

        // Arena setup
        List<Integer> arenaIDList = new ArrayList<>();
        try (Connection connection = getDataSource().getConnection()) {
            String getArenaQuery = "SELECT ArenaID FROM Arenas";

            try (PreparedStatement preparedStatement = connection.prepareStatement(getArenaQuery)) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int arenaID = resultSet.getInt("ArenaID");// Retrieve data by column name
                    arenaIDList.add(arenaID);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        for (int i : arenaIDList) {
            try (Connection connection = getDataSource().getConnection()) {
                String getArenaQuery = "SELECT * FROM Blocks AS b WHERE b.ArenaID = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(getArenaQuery)) {
                    preparedStatement.setInt(1, i);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    resultSet.next();
                    String world = resultSet.getString("world");
                    arenaManager.addArena(new Arena(i, this, Bukkit.getWorld(world)));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    try (Connection connection = getDataSource().getConnection()) {
                        String getArenaQuery = "SELECT * FROM Blocks AS b WHERE b.ArenaID = ?";
                        try (PreparedStatement preparedStatement = connection.prepareStatement(getArenaQuery)) {
                            preparedStatement.setInt(1, i);
                            ResultSet resultSet = preparedStatement.executeQuery();
                            while (resultSet.next()) {
                                int x = resultSet.getInt("x");// Retrieve data by column name
                                int y = resultSet.getInt("y");
                                int z = resultSet.getInt("z");
                                String material = resultSet.getString("block_material");
                                String world = resultSet.getString("world");
                                Block b = Bukkit.getWorld(world).getBlockAt(x, y, z);
                                if (b.getX() == -87 && b.getY() == 88 && b.getZ() == -170) {
                                    System.out.println("setting " + material);
                                    System.out.println(Material.getMaterial(material));
                                }
                                b.getState().setType(Material.getMaterial(material));
                                arenaManager.getArena(i).addArenaBlock(b);
                            }
                        }
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }.runTaskAsynchronously(this);
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info(ChatColor.RED + "Disabled " + this.getName());
    }

    private void testDataSource(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            if (!conn.isValid(1)) {
                throw new SQLException("Could not establish database connection.");
            } else {
                System.out.println("CONNECTED TO MYSQL.");
            }
        }
    }

    private MysqlConnectionPoolDataSource initMySQLDataSource(Database db) throws SQLException {
        dataSource = new MysqlConnectionPoolDataSource();
        // set credentials
        dataSource.setServerName(db.getHost());
        dataSource.setPortNumber(db.getPort());
        dataSource.setUser(db.getUser());
        dataSource.setPassword(db.getPassword());

        try (Connection connection = dataSource.getConnection()) {
            String dbName = db.getDatabaseName();
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + dbName;

            try (PreparedStatement preparedStatement = connection.prepareStatement(createDatabaseQuery)) {
                preparedStatement.executeUpdate();
                System.out.println("Database created successfully.");
            }
        }
        dataSource.setDatabaseName(db.getDatabaseName());
        // Test connection
        testDataSource(dataSource);
        return dataSource;
    }

    private void initDb() throws SQLException, IOException {
        // first lets read our setup file.
        // This file contains statements to create our inital tables.
        // it is located in the resources.
        String setup;
        try (InputStream in = getClassLoader().getResourceAsStream("dbsetup.sql")) {
            // Java 9+ way
            setup = new String(in.readAllBytes());
        } catch (IOException e) {
            getLogger().log(Level.SEVERE, "Could not read db setup file.", e);
            throw e;
        }
        // Mariadb can only handle a single query per statement. We need to split at ;.
        String[] queries = setup.split(";");
        // execute each query to the database.
        for (String query : queries) {
            // If you use the legacy way you have to check for empty queries here.
            if (query.isBlank()) continue;
            System.out.println("executing " + query);
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.execute();
            }
        }
        db.setDataSource(dataSource);
        getLogger().info("§2Database setup complete.");
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public MysqlConnectionPoolDataSource getDataSource() {
        return dataSource;
    }
}
