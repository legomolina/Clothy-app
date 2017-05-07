package utils;


import config.Constants;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {
    private static Connection connection;

    public static Connection getConnection() {
        try {
            Class.forName(Constants.Database.JDBC_DRIVER);

            if(connection == null)
                connection = DriverManager.getConnection(Constants.Database.URL, Constants.Database.USER, Constants.Database.PASSWORD);

            return connection;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void closeConnection() {
        if(connection != null)
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
