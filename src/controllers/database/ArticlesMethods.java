package controllers.database;

import models.Article;
import models.Brand;
import models.Category;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArticlesMethods extends DatabaseMethods {
    public static ArrayList<Article> getAllArticles() {
        ArrayList<Article> articles = new ArrayList<>();
        ArrayList<Category> categories;

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

                ResultSet categoriesResult = categoriesStatement.executeQuery();
                categories = new ArrayList<>();

                //Put every category
                while (categoriesResult.next())
                    categories.add(new Category(categoriesResult.getInt("category_id"), categoriesResult.getString("category_name"), categoriesResult.getString("category_description")));

                //Set categories in the article
                currentArticle.setCategories(categories);

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