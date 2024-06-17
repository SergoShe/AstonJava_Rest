package com.github.SergoShe.util;

import com.github.SergoShe.config.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final DataSource dataSource = DatabaseConfig.getDataSource();

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
