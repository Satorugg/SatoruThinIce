package io.github.satorugg.satoruspleef;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.satorugg.satoruspleef.commands.SetArenaCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getLogger;

public class SatoruSpleef extends JavaPlugin {

    private DataSource dataSource;

    @Override
    public void onEnable() {
        Bukkit.getLogger().info(ChatColor.GREEN + "Enabled " + this.getName());
        FileConfiguration config = getConfig();
        Database db = new Database(config);
        try {
            dataSource = initMySQLDataSource(db);
            initDb();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.getCommand("set-arena").setExecutor(new SetArenaCommand());
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

    private DataSource initMySQLDataSource(Database db) throws SQLException {
        MysqlDataSource dataSource = new MysqlConnectionPoolDataSource();
        // set credentials
        dataSource.setServerName(db.getHost());
        dataSource.setPortNumber(db.getPort());
        dataSource.setDatabaseName(db.getDatabaseName());
        dataSource.setUser(db.getUser());
        dataSource.setPassword(db.getPassword());

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
        getLogger().info("§2Database setup complete.");
    }
}
