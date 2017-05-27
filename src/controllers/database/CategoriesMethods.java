package controllers.database;


import models.Category;
import views.categories.Categories;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriesMethods extends DatabaseMethods {
    private static final String REMOVE_CATEGORY_NAME = "Sin categoría";

    public static ArrayList<Category> getArticleCategories(int articleId) {
        ArrayList<Category> categories = new ArrayList<>();
        String sqlQuery = "SELECT categories.* FROM categories, articles, categories_articles_map" +
                " WHERE articles.article_id = categories_articles_map.article_id AND categories_articles_map.category_id = categories.category_id" +
                " AND articles.article_id = ?";

        try {
            PreparedStatement categoriesStatement = connection.prepareStatement(sqlQuery);
            categoriesStatement.setInt(1, articleId);

            ResultSet categoriesResult = categoriesStatement.executeQuery();
            categories = new ArrayList<>();

            //Put every category
            while (categoriesResult.next())
                categories.add(new Category(categoriesResult.getInt("category_id"), categoriesResult.getString("category_name"), categoriesResult.getString("category_description")));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return categories;
    }

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