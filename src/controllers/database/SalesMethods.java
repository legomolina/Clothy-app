package controllers.database;

import models.Article;
import models.Sale;
import models.SaleLine;

import java.awt.datatransfer.MimeTypeParseException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesMethods extends DatabaseMethods {
    public static ArrayList<SaleLine> getSaleLines(Sale sale) {
        ArrayList<SaleLine> lines = new ArrayList<>();
        String sqlQuery = "SELECT * FROM sale_lines WHERE sale_line_sale = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setInt(1, sale.getId());
            ResultSet result = statement.executeQuery();

            while (result.next())
                lines.add(new SaleLine(result.getInt("sale_line_id"), ArticlesMethods.getArticle(result.getInt("sale_line_article")),
                        sale, result.getFloat("sale_line_discount"), result.getInt("sale_line_quantity")));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return lines;
    }

    public static ArrayList<Sale> getAllSales() {
        ArrayList<Sale> sales = new ArrayList<>();
        String sqlQuery = "SELECT * FROM sales";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            while (result.next())
                sales.add(new Sale(result.getInt("sale_id"),
                        EmployeesMethods.getEmployee(result.getInt("sale_employee")),
                        ClientsMethods.getClient(result.getInt("sale_client")),
                        result.getDate("sale_date"), result.getString("sale_payment")));

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return sales;
    }

    public static void addSales(Sale... addSale) {
        String sqlQuery = "INSERT INTO sales VALUES(?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Sale s : addSale) {
                statement.setInt(1, s.getId());
                statement.setInt(2, s.getClient().getId());
                statement.setInt(3, s.getEmployee().getId());
                statement.setDate(4, new Date(s.getDate().getTime()));
                statement.setString(5, s.getPayment());

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

    public static void updateSales(Sale... updateSale) {
        String sqlQuery = "UPDATE sales SET sale_employee = ?, sale_client = ?, sale_date = ?, sale_payment = ? WHERE sale_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for (Sale s : updateSale) {
                statement.setInt(1, s.getEmployee().getId());
                statement.setInt(2, s.getClient().getId());
                statement.setDate(3, (Date) s.getDate());
                statement.setString(4, s.getPayment());
                statement.setInt(5, s.getId());

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

    public static void removeSaleLines(List<SaleLine> removeLines) {
        removeSaleLines(removeLines.toArray(new SaleLine[removeLines.size()]));
    }

    public static void removeSaleLines(SaleLine... removeLines) {
        String sqlQuery = "DELETE FROM sale_lines WHERE sale_line_id = ? AND sale_line_sale = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(SaleLine s : removeLines) {
                statement.setInt(1, s.getId());
                statement.setInt(2, s.getSale().getId());

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
