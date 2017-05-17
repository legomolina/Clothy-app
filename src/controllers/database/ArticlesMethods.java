package controllers.database;

import javafx.collections.FXCollections;
import models.Article;
import models.Brand;
import models.Category;
import models.Size;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticlesMethods extends DatabaseMethods {
    public static ArrayList<Article> getAllArticles() {
        ArrayList<Article> articles = new ArrayList<>();
        //ArrayList<Category> categories;

        String sqlQuery = "SELECT articles.*, brands.* FROM articles, brands WHERE articles.article_brand = brands.brand_id";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            //Every article
            while (result.next()) {
                //Save its id and create new object
                int articleId = result.getInt("article_id");
                Article currentArticle = new Article(articleId);

                //Set simple values
                currentArticle.setCode(result.getString("article_code"));
                currentArticle.setDescription(result.getString("article_description"));
                currentArticle.setPrice(result.getFloat("article_price"));
                currentArticle.setName(result.getString("article_name"));
                currentArticle.setBrand(new Brand(result.getInt("brand_id"), result.getString("brand_name"),
                        result.getString("brand_address"), result.getString("brand_email"), result.getString("brand_phone")));

                //For categories
                String categoriesQuery = "SELECT categories.* FROM categories, articles, categories_articles_map" +
                        " WHERE articles.article_id = categories_articles_map.article_id AND categories_articles_map.category_id = categories.category_id" +
                        " AND articles.article_id = ?";

                PreparedStatement categoriesStatement = connection.prepareStatement(categoriesQuery);
                categoriesStatement.setInt(1, articleId);

                ResultSet categoriesResult = statement.executeQuery();
                categories = new ArrayList<>();

                //Put every category
                while (categoriesResult.next())
                    categories.add(new Category(categoriesResult.getInt("category_id"), categoriesResult.getString("category_description"), categoriesResult.getString("category_name")));

                //Set categories in the article
                currentArticle.setCategories(FXCollections.observableArrayList(categories));

                //Add current article to the list
                articles.add(currentArticle);
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return articles;
    }

    public static ArrayList<ArticleStockInfo> getStock(Article article) {
        ArrayList<ArticleStockInfo> articleStock = new ArrayList<>();
        String sqlQuery = "SELECT article_stock, size_id FROM sizes_articles_map WHERE article_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, article.getId());
            ResultSet result = statement.executeQuery();

            while(result.next())
                articleStock.add(new ArticleStockInfo(SizesMethods.getSize(result.getInt("size_id")), article, result.getInt("article_stock")));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return articleStock;
    }

    //TODO finish this
    public static void addArticles(List<Size> sizes, List<Category> categories, Article... addArticle) {
        String sqlQuery = "INSERT INTO articles (article_id, article_name, article_code, article_description, " +
                "article_price, article_brand) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Article a : addArticle) {
                statement.setInt(1, a.getId());
                statement.setString(2, a.getName());
                statement.setString(3, a.getCode());
                statement.setString(4, a.getDescription());
                statement.setFloat(5, a.getPrice());
                statement.setInt(6, a.getBrand().getId());

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