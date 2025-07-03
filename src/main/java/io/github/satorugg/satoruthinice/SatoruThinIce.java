package io.github.satorugg.satoruthinice;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.satorugg.satoruthinice.commands.ThinIceCommand;
import io.github.satorugg.satoruthinice.game.ArenaManager;
import io.github.satorugg.satoruthinice.listeners.admin.SetArenaListener;
import io.github.satorugg.satoruthinice.listeners.user.ThinIceGameListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        db = new Database(config);
        try {
            dataSource = initMySQLDataSource(db);
            initDb();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        getCommand("sthinice").setExecutor(new ThinIceCommand(this));

        getServer().getPluginManager().registerEvents(new SetArenaListener(this), this);
        getServer().getPluginManager().registerEvents(new ThinIceGameListener(), this);
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
                System.out.println("CONNECTED TO MYSQL!");
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

        System.out.println("running initMYSQLDATASOURCE");

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
