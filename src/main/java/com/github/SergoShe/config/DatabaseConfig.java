package com.github.SergoShe.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DatabaseConfig {
    private static final Properties properties = new Properties();

    private DatabaseConfig() {
    }

    static {
        try (InputStream input = DatabaseConfig.class.getClassLoader().getResourceAsStream("/db.properties")) {
            if (input != null) {
                throw new IOException("Unable to find db.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DataSource getDataSource() {
        HikariConfig config = new HikariConfig("/db.properties");
        return new HikariDataSource(config);
    }
}
