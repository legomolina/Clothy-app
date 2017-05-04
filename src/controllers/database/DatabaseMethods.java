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

    public static void updateEmployees(Employee updateEmployee) {
        String sqlQuery = "UPDATE employees SET employee_name = ?, employee_surname = ?, employee_address = ?," +
                "employee_email = ?, employee_phone = ?, employee_login_name = ?, employee_login_type = ?," +
                "employee_is_active = ? WHERE employee_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            statement.setString(1, updateEmployee.getName().getValue());
            statement.setString(2, updateEmployee.getSurname().getValue());
            statement.setString(3, updateEmployee.getAddress().getValue());
            statement.setString(4, updateEmployee.getEmail().getValue());
            statement.setString(5, updateEmployee.getPhone().getValue());
            statement.setString(6, updateEmployee.getLoginName().getValue());
            statement.setString(7, updateEmployee.getLoginType().getValue());
            statement.setInt(8, updateEmployee.getLoginActive().getValue() ? 1 : 0);
            statement.setInt(9, updateEmployee.getId().getValue());

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
