package controllers.database;

import models.Article;
import models.Brand;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ArticlesMethods extends DatabaseMethods {
    public static ArrayList<Article> getAllArticles() {
        ArrayList<Article> articles = new ArrayList<>();

        String sqlQuery =
                "SELECT articles.*, brands.*, categories.*, sizes.*, sizes_articles_map.article_stock " +
                "FROM articles, brands, categories, sizes, sizes_articles_map, categories_articles_map " +
                "WHERE articles.article_id = sizes_articles_map.article_id " +
                    "AND sizes_articles_map.size_id = sizes.size_id " +
                    "AND articles.article_id = categories_articles_map.article_id " +
                    "AND categories_articles_map.category_id = categories.category_id " +
                    "AND articles.article_brand = brands.brand_id;";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                brands.add(new Brand(result.getInt("brand_id"), result.getString("brand_name"),
                        result.getString("brand_address"), result.getString("brand_email"),
                        result.getString("brand_phone")));
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return brands;
    }
}