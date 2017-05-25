package controllers.database;


import models.Size;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SizesMethods extends DatabaseMethods {
    private static final String REMOVE_SIZE_VALUE = "Sin talla";

    public static ArrayList<Size> getAllSizes() {
        ArrayList<Size> sizes = new ArrayList<>();

        String sqlQuery = "SELECT * FROM sizes WHERE size_value <> '" + REMOVE_SIZE_VALUE + "'";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                sizes.add(new Size(result.getInt("size_id"), result.getString("size_value")));
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return sizes;
    }

    public static Size getSize(int id) {
        Size size = null;

        String sqlQuery = "SELECT * FROM sizes WHERE size_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, id);
            ResultSet result = statement.executeQuery();

            result.first();
            size = new Size(result.getInt("size_id"), result.getString("size_value"));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return size;
    }

    public static void addSizes(Size... addSizes) {
        String sqlQuery = "INSERT INTO sizes (size_id, size_value) VALUES (?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Size s : addSizes) {
                statement.setInt(1, s.getId());
                statement.setString(2, s.getSize());

                statement.executeUpdate();
            }
        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }
    }

    public static void updateSizes(Size... updateSizes) {
        String sqlQuery = "UPDATE sizes SET size_value = ? WHERE size_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Size s : updateSizes) {
                statement.setString(1, s.getSize());
                statement.setInt(2, s.getId());

                statement.executeUpdate();
            }
        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }
    }

    public static void removeSizes(List<Size> removeSizes) {
        removeSizes(removeSizes.toArray(new Size[removeSizes.size()]));
    }

    public static void removeSizes(Size... removeSizes) {
        //Set size value as 'Sin talla' in order to don't remove articles with that size
        String sqlQuery = "UPDATE sizes SET size_value = '" + REMOVE_SIZE_VALUE + "' WHERE size_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Size s : removeSizes) {
                statement.setInt(1, s.getId());
                statement.executeUpdate();
            }
        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }
    }
}
