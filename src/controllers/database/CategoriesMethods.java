package controllers.database;


import models.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesMethods extends DatabaseMethods {
    private static final String REMOVE_CATEGORY_NAME = "Sin categoría";

    public static ArrayList<Category> getAllCategories() {
        ArrayList<Category> categories = new ArrayList<>();

        String sqlQuery = "SELECT * FROM categories WHERE category_name <> '" + REMOVE_CATEGORY_NAME + "'";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                categories.add(new Category(result.getInt("category_id"), result.getString("category_name"),
                        result.getString("category_description")));
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return categories;
    }

    public static void addCategories(Category... addCategories) {
        String sqlQuery = "INSERT INTO categories (category_id, category_name, category_description) VALUES (?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Category c : addCategories) {
                statement.setInt(1, c.getId());
                statement.setString(2, c.getName());
                statement.setString(3, c.getDescription());

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

    public static void updateCategories(Category... updateCategories) {
        String sqlQuery = "UPDATE categories SET category_name = ?, category_description = ? WHERE category_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Category c : updateCategories) {
                statement.setString(1, c.getName());
                statement.setString(2, c.getDescription());
                statement.setInt(3, c.getId());

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

    public static void removeCategories(List<Category> removeCategories) {
        removeCategories(removeCategories.toArray(new Category[removeCategories.size()]));
    }

    public static void removeCategories(Category... removeCategories) {
        String sqlQuery = "UPDATE categories SET category_name = '" + REMOVE_CATEGORY_NAME + "' WHERE category_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Category c : removeCategories) {
                statement.setInt(1, c.getId());
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