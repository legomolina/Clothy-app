package controllers.database;

import models.Article;
import models.Brand;
import models.Category;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticlesMethods extends DatabaseMethods {

    public static Article getArticle(int articleId) {
        String sqlQuery = "SELECT articles.* FROM articles WHERE articles.article_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, articleId);
            ResultSet result = statement.executeQuery();

            if(result.next()) {
                return new Article(result.getInt("article_id"), result.getString("article_code"),
                        result.getString("article_name"), result.getString("article_description"),
                        CategoriesMethods.getArticleCategories(result.getInt("article_id")),
                        BrandsMethods.getBrand(result.getInt("article_brand")), result.getFloat("article_price"));
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Article> getArticlesByCode(String articleCode) {
        ArrayList<Article> articles = new ArrayList<>();
        String sqlQuery = "SELECT * FROM articles WHERE article_code = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, articleCode);
            ResultSet result = statement.executeQuery();

            while(result.next())
                articles.add(new Article(result.getInt("article_id"), result.getString("article_code"),
                        result.getString("article_name"), result.getString("article_description"),
                        CategoriesMethods.getArticleCategories(result.getInt("article_id")),
                        BrandsMethods.getBrand(result.getInt("article_brand")), result.getFloat("article_price")));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return articles;
    }

    public static ArrayList<Article> getAllArticles() {
        ArrayList<Article> articles = new ArrayList<>();
        ArrayList<Category> categories;

        String sqlQuery = "SELECT articles.* FROM articles";
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
                currentArticle.setBrand(BrandsMethods.getBrand(result.getInt("article_brand")));

                //Set categories in the article
                currentArticle.setCategories(CategoriesMethods.getArticleCategories(articleId));

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

    public static void addArticles(Article... addArticle) {
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

                addCategories(a.getId(), a.getCategories());
            }
        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }
    }

    public static void updateArticles(Article... updateArticles) {
        String sqlQuery = "UPDATE articles SET article_name = ?, article_code = ?, article_description = ?, article_brand = ?, " +
                "article_price = ?  WHERE article_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Article a : updateArticles) {
                statement.setString(1, a.getName());
                statement.setString(2, a.getCode());
                statement.setString(3, a.getDescription());
                statement.setInt(4, a.getBrand().getId());
                statement.setFloat(5, a.getPrice());
                statement.setInt(6, a.getId());

                statement.executeUpdate();

                addCategories(a.getId(), a.getCategories());
            }
        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }
    }

    public static void removeArticles(List<Article> removeArticle) {
        removeArticles(removeArticle.toArray(new Article[removeArticle.size()]));
    }

    public static void removeArticles(Article... removeArticle) {
        String sqlQuery = "DELETE FROM articles WHERE article_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Article a : removeArticle) {
                removeCategories(a.getId());

                statement.setInt(1, a.getId());
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

    private static void addCategories(int articleId, List<Category> categories) {
        removeCategories(articleId);

        String sqlQuery = "INSERT INTO categories_articles_map (article_id, category_id) VALUES (?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Category c : categories) {
                statement.setInt(1, articleId);
                statement.setInt(2, c.getId());

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

    private static void removeCategories(int articleId) {
        String sqlQuery = "DELETE FROM categories_articles_map WHERE article_id = ?";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            statement.setInt(1, articleId);
            statement.executeUpdate();

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }
    }
}