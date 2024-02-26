package io.github.satorugg.satoruspleef;

import org.bukkit.configuration.file.FileConfiguration;

public class Database {

    private String host;
    private int port;
    private String databaseName;
    private String user;
    private String password;

    public Database(FileConfiguration config) {
        this.host = config.getString("database.host");
        this.port = config.getInt("database.port");
        this.databaseName = config.getString("database.database");
        this.user = config.getString("database.user");
        this.password = config.getString("database.password");
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
