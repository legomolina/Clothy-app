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
}
