package controllers.database;


import models.Employee;
import utils.DatabaseHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseMethods {
    private final static Connection connection = DatabaseHandler.getConnection();

    public static int getLastId(String tableName, String columnName) {
        String sqlQuery = "SELECT * FROM " + tableName + " ORDER BY " + columnName + " DESC LIMIT 1";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();
            result.first();

            return result.getInt(columnName);

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return -1;
    }

    public static ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> employees = new ArrayList<>();

        String sqlQuery = "SELECT * FROM employees";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                employees.add(new Employee(result.getInt("employee_id"), result.getString("employee_name"),
                        result.getString("employee_surname"), result.getString("employee_address"),
                        result.getString("employee_email"), result.getString("employee_phone"),
                        result.getString("employee_login_name"), result.getString("employee_login_password"),
                        result.getString("employee_login_type"), result.getInt("employee_is_active") > 0));
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return employees;
    }

    public static void addEmployees(Employee... addEmployee){
        String sqlQuery = "INSERT INTO employees (employee_name, employee_surname, employee_address, employee_email, " +
                "employee_phone, employee_login_name, employee_login_password, employee_login_type, " +
                "employee_is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(Employee e : addEmployee) {
                statement.setString(1, e.getName());
                statement.setString(2, e.getSurname());
                statement.setString(3, e.getAddress());
                statement.setString(4, e.getEmail());
                statement.setString(5, e.getPhone());
                statement.setString(6, e.getLoginName());
                statement.setString(7, "asd");
                statement.setString(8, e.getLoginType());
                statement.setInt(9, e.isLoginActive() ? 1 : 0);

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

    public static void updateEmployees(Employee... updateEmployees) {
        String sqlQuery = "UPDATE employees SET employee_name = ?, employee_surname = ?, employee_address = ?," +
                "employee_email = ?, employee_phone = ?, employee_login_name = ?, employee_login_type = ?," +
                "employee_is_active = ? WHERE employee_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(Employee e : updateEmployees) {
                statement.setString(1, e.getName());
                statement.setString(2, e.getSurname());
                statement.setString(3, e.getAddress());
                statement.setString(4, e.getEmail());
                statement.setString(5, e.getPhone());
                statement.setString(6, e.getLoginName());
                statement.setString(7, e.getLoginType());
                statement.setInt(8, e.isLoginActive() ? 1 : 0);
                statement.setInt(9, e.getId());

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

    public static void removeEmployees(Employee... removeEmployees) {
        String sqlQuery = "DELETE FROM employees WHERE employee_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(Employee e : removeEmployees) {
                statement.setInt(1, e.getId());
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
