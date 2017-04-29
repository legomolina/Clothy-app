package utils;


import config.Constants;

import java.sql.*;

public class DatabaseHandler {
    public static Connection getConnection() {
        try {
            Class.forName(Constants.Database.JDBC_DRIVER);
            return DriverManager.getConnection(Constants.Database.URL, Constants.Database.USER, Constants.Database.PASSWORD);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}
