package it.wintry.customblocks.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.wintry.customblocks.CustomBlocks;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private HikariDataSource dataSource;

    private String hostname, port, database, username, password;

    public ConnectionPoolManager(CustomBlocks plugin) {
        init(plugin.getConfig());
        setupPool();
    }

    private void init(FileConfiguration config) {
        hostname = config.getString("mysql.host");
        port = config.getString("mysql.port");
        database = config.getString("mysql.database");
        username = config.getString("mysql.username");
        password = config.getString("mysql.password");
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + database);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setPoolName("CustomBlocks");
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}