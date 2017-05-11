package controllers.database;


import utils.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseMethods {
    protected final static Connection connection = DatabaseHandler.getConnection();

    public static int getLastId(String tableName, String columnName) {
        String sqlQuery = "SELECT * FROM " + tableName + " ORDER BY " + columnName + " DESC LIMIT 1";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            return (!result.first()) ? 0 : result.getInt(columnName);

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return -1;
    }
}
