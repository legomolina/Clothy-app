package controllers.database;

import models.Client;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientsMethods extends DatabaseMethods {
    public static ArrayList<Client> getAllClients() {
        ArrayList<Client> clients = new ArrayList<>();

        String sqlQuery = "SELECT * FROM clients";
        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);
            ResultSet result = statement.executeQuery();

            while (result.next()) {
                clients.add(new Client(result.getInt("client_id"), result.getString("client_nif"), result.getString("client_name"),
                        result.getString("client_surname"), result.getString("client_address"),
                        result.getString("client_email"), result.getString("client_phone")));
            }

        } catch (NullPointerException e) {
            System.out.println("An error occurred with Database connection");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("An error occurred preparing the Query: " + sqlQuery);
            e.printStackTrace();
        }

        return clients;
    }

    public static void addClients(Client... addClients){
        String sqlQuery = "INSERT INTO clients (client_id, client_nif, client_name, client_surname, client_address, client_email, " +
                "client_phone) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(Client c : addClients) {
                statement.setInt(1, c.getId());
                statement.setString(2, c.getNif());
                statement.setString(3, c.getName());
                statement.setString(4, c.getSurname());
                statement.setString(5, c.getAddress());
                statement.setString(6, c.getEmail());
                statement.setString(7, c.getPhone());

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

    public static void updateClients(Client... updateClients) {
        String sqlQuery = "UPDATE clients SET client_name = ?, client_surname = ?, client_address = ?," +
                "client_email = ?, client_phone = ?, client_nif = ? WHERE client_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(Client c : updateClients) {
                statement.setString(1, c.getName());
                statement.setString(2, c.getSurname());
                statement.setString(3, c.getAddress());
                statement.setString(4, c.getEmail());
                statement.setString(5, c.getPhone());
                statement.setString(6, c.getNif());
                statement.setInt(7, c.getId());

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

    public static void removeClients(List<Client> removeClients) {
        removeClients(removeClients.toArray(new Client[removeClients.size()]));
    }

    public static void removeClients(Client... removeClients) {
        String sqlQuery = "DELETE FROM clients WHERE client_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sqlQuery);

            for(Client c : removeClients) {
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
