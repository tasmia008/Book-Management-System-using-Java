/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookmanagementsystem;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class BookManagementSystem extends JFrame implements RESTfulAPI{
    private JTextField bookNameField;
    private JTextField publisherNameField;
    private JTextField publisherAgeField;
    private JTextField pageNoField;
    private JTextField publishDateField;
    private JComboBox<String> bookTypeComboBox;

    public BookManagementSystem() {
        setTitle("Book Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(7, 2, 10, 10));

        JLabel bookNameLabel = new JLabel("Book Name:");
        bookNameField = new JTextField();

        JLabel publisherNameLabel = new JLabel("Publisher Name:");
        publisherNameField = new JTextField();

        JLabel publisherAgeLabel = new JLabel("Publisher Age:");
        publisherAgeField = new JTextField();

        JLabel pageNoLabel = new JLabel("Page No:");
        pageNoField = new JTextField();

        JLabel publishDateLabel = new JLabel("Publish Date:");
        publishDateField = new JTextField();

        JLabel bookTypeLabel = new JLabel("Book Type:");
        String[] bookTypes = { "Sci-fi", "Drama", "Novel" };
        bookTypeComboBox = new JComboBox<>(bookTypes);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Retrieve the entered values
                String bookName = bookNameField.getText();
                String publisherName = publisherNameField.getText();
                int publisherAge = Integer.parseInt(publisherAgeField.getText());
                int pageNo = Integer.parseInt(pageNoField.getText());
                String publishDate = publishDateField.getText();
                String bookType = (String) bookTypeComboBox.getSelectedItem();

                try {
                    // Establish the connection
                    String url = "jdbc:derby://localhost:1527/books";
                    String username = "rivu";
                    String password = "rivu";
                    Connection connection = DriverManager.getConnection(url, username, password);

                    // Perform the database operation
                    String sql = "INSERT INTO books (book_name, publisher_name, publisher_age, page_no, publish_date, book_type) VALUES (?, ?, ?, ?, ?, ?)";

                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setString(1, bookName);
                    statement.setString(2, publisherName);
                    statement.setInt(3, publisherAge);
                    statement.setInt(4, pageNo);
                    statement.setString(5, publishDate);
                    statement.setString(6, bookType);
                    statement.executeUpdate();

                    // Close the connection and statement
                    statement.close();
                    connection.close();

                    // Show a success message
                    JOptionPane.showMessageDialog(BookManagementSystem.this,
                            "Data saved successfully", "Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    // Show an error message
                    JOptionPane.showMessageDialog(BookManagementSystem.this,
                            "An error occurred while saving data: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Add components to the frame
        add(bookNameLabel);
        add(bookNameField);
        add(publisherNameLabel);
        add(publisherNameField);
        add(publisherAgeLabel);
        add(publisherAgeField);
        add(pageNoLabel);
        add(pageNoField);
        add(publishDateLabel);
        add(publishDateField);
        add(bookTypeLabel);
        add(bookTypeComboBox);
        add(new JLabel()); // Empty label for spacing
        add(saveButton);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BookManagementSystem();
            }
        });
    }

    @Override
    public void get() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void post() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void put() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
