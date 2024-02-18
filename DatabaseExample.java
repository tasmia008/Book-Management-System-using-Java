/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookmanagementsystem;


import java.sql.*;

public class DatabaseExample {
    public static void main(String[] args) {
        try {
            // Establish the connection
            String url = "jdbc:derby://localhost:1527/books";
            String username = "rivu";
            String password = "rivu";
            Connection connection = DriverManager.getConnection(url, username, password);

            String sql = "CREATE TABLE books (" +
                    "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, " +
                    "book_name VARCHAR(100), " +
                    "publisher_name VARCHAR(100), " +
                    "publisher_age INT, " +
                    "page_no INT, " +
                    "publish_date DATE, " +
                    "book_type VARCHAR(20)" +
                    ")";
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);

            // Close the connection and statement
            statement.close();
            connection.close();
            
            System.out.println("Table created successfully!");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

