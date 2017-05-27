package controllers.database;


import models.Brand;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BrandsMethods extends DatabaseMethods {
    private static final String REMOVE_BRAND_NAME = "Sin marca";

    public static Brand getBrand(int brandId) {
        String sqlQuery = "SELECT * FROM brands WHERE brand_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, brandId);
            ResultSet result = statement.executeQuery();

            if(result.next())
                return new Brand(result.getInt("brand_id"), result.getString("brand_name"),
                        result.getString("brand_address"), result.getString("brand_email"),
                        result.getString("brand_phone"));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return null;
    }

    public static ArrayList<Brand> getAllBrands() {
        ArrayList<Brand> brands = new ArrayList<>();

        String sqlQuery = "SELECT * FROM brands WHERE brand_name <> '" + REMOVE_BRAND_NAME + "'";
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

    public static void addBrands(Brand... addBrands) {
        String sqlQuery = "INSERT INTO brands (brand_id, brand_name, brand_address, brand_email, " +
                "brand_phone) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Brand b : addBrands) {
                statement.setInt(1, b.getId());
                statement.setString(2, b.getName());
                statement.setString(3, b.getAddress());
                statement.setString(4, b.getEmail());
                statement.setString(5, b.getPhone());

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

    public static void updateBrands(Brand... updateBrands) {
        String sqlQuery = "UPDATE brands SET brand_name = ?, brand_address = ?," +
                "brand_email = ?, brand_phone = ? WHERE brand_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Brand b : updateBrands) {
                statement.setString(1, b.getName());
                statement.setString(2, b.getAddress());
                statement.setString(3, b.getEmail());
                statement.setString(4, b.getPhone());
                statement.setInt(5, b.getId());

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

    public static void removeBrands(List<Brand> removeBrands) {
        removeBrands(removeBrands.toArray(new Brand[removeBrands.size()]));
    }

    public static void removeBrands(Brand... removeBrands) {
        String sqlQuery = "UPDATE brands SET brand_name = '" + REMOVE_BRAND_NAME + "' WHERE brand_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Brand b : removeBrands) {
                statement.setInt(1, b.getId());
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