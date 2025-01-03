package org.example.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static HikariDataSource dataSource;
    final static Logger logger = LogManager.getLogger(DatabaseManager.class);
    private DatabaseManager() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:postgresql://localhost:5432/db_sem01");
            config.setUsername("Ekaterina");
            config.setPassword("eka_rina16");
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            dataSource = new HikariDataSource(config);
            Flyway flyway = Flyway.configure().dataSource(dataSource).load();
            logger.info("Start migration");
            flyway.migrate();
            logger.info("Migration done");
        } catch (FlywayException e) {
            logger.error("Flyway migration error", e);
        } catch (IllegalArgumentException e) {
            logger.error("Configuration error", e);
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

}
