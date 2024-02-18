/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookmanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookSearchSystem extends JFrame implements RESTfulAPI{
    private JTextField keywordField;
    private JComboBox<String> ageComboBox;
    private JCheckBox sciFiCheckBox;
    private JCheckBox dramaCheckBox;
    private JCheckBox novelCheckBox;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private int currentPage = 1;
    private int totalPages = 0;

    public BookSearchSystem() {
    setTitle("Book Search System");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 400);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JPanel searchPanel = createSearchPanel();
    add(searchPanel, BorderLayout.NORTH);

    tableModel = new DefaultTableModel(new Object[]{"Sl.", "Book Name", "Publisher Name", "Date"}, 0);
    resultTable = new JTable(tableModel);
    JScrollPane scrollPane = new JScrollPane(resultTable);
    add(scrollPane, BorderLayout.CENTER);

    JPanel paginationPanel = createPaginationPanel();
    add(paginationPanel, BorderLayout.SOUTH);

    // Load all data initially
    performSearch();

    setVisible(true);
}


    private JPanel createSearchPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());

    JLabel keywordLabel = new JLabel("Keyword:");
    keywordField = new JTextField(20);

    JLabel ageLabel = new JLabel("Age:");
    String[] ageOptions = {"Any", "Child", "Teenager", "Adult"};
    ageComboBox = new JComboBox<>(ageOptions);

    JLabel bookTypeLabel = new JLabel("Book Types:");
    sciFiCheckBox = new JCheckBox("Sci-fi");
    dramaCheckBox = new JCheckBox("Drama");
    novelCheckBox = new JCheckBox("Novel");

    JButton searchButton = new JButton("Search");
    searchButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            performSearch();
        }
    });

    panel.add(keywordLabel);
    panel.add(keywordField);
    panel.add(ageLabel);
    panel.add(ageComboBox);
    panel.add(bookTypeLabel);
    panel.add(sciFiCheckBox);
    panel.add(dramaCheckBox);
    panel.add(novelCheckBox);
    panel.add(searchButton);

    return panel;
}


    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton prevButton = new JButton("Previous");
        prevButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    performSearch();
                }
            }
        });

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currentPage < totalPages) {
                    currentPage++;
                    performSearch();
                }
            }
        });

        panel.add(prevButton);
        panel.add(nextButton);

        return panel;
    }

    private void performSearch() {
        String keyword = keywordField.getText();
        String age = (String) ageComboBox.getSelectedItem();
        boolean sciFiSelected = sciFiCheckBox.isSelected();
        boolean dramaSelected = dramaCheckBox.isSelected();
        boolean novelSelected = novelCheckBox.isSelected();

        // Clear previous search results
        tableModel.setRowCount(0);

        try {
            // Establish the connection
            String url = "jdbc:derby://localhost:1527/books";
            String username = "rivu";
            String password = "rivu";
            Connection connection = DriverManager.getConnection(url, username, password);

                        // Prepare the SQL query
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT COUNT(*) FROM books WHERE 1=1");

            if (!keyword.isEmpty()) {
                sqlBuilder.append(" AND (book_name LIKE ? OR publisher_name LIKE ?)");
            }

            if (!age.equals("Any")) {
                sqlBuilder.append(" AND publisher_age_group = ?");
            }

            if (sciFiSelected || dramaSelected || novelSelected) {
                sqlBuilder.append(" AND (");

                if (sciFiSelected) {
                    sqlBuilder.append("book_type = 'Sci-fi' OR ");
                }

                if (dramaSelected) {
                    sqlBuilder.append("book_type = 'Drama' OR ");
                }

                if (novelSelected) {
                    sqlBuilder.append("book_type = 'Novel' OR ");
                }

                sqlBuilder.delete(sqlBuilder.length() - 4, sqlBuilder.length());
                sqlBuilder.append(")");
            }

            // Execute the count query to get the total number of records
            PreparedStatement countStatement = connection.prepareStatement(sqlBuilder.toString());
            int paramIndex = 1;

            if (!keyword.isEmpty()) {
                String keywordPattern = "%" + keyword + "%";
                countStatement.setString(paramIndex++, keywordPattern);
                countStatement.setString(paramIndex++, keywordPattern);
            }

            if (!age.equals("Any")) {
                countStatement.setString(paramIndex++, age);
            }

            ResultSet countResultSet = countStatement.executeQuery();
            if (countResultSet.next()) {
                int totalRecords = countResultSet.getInt(1);
                totalPages = (int) Math.ceil((double) totalRecords / 10); // Assuming 10 records per page
            }

            countResultSet.close();
            countStatement.close();

            // Prepare the SQL query to retrieve the actual records
            sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT * FROM books WHERE 1=1");

            if (!keyword.isEmpty()) {
                sqlBuilder.append(" AND (book_name LIKE ? OR publisher_name LIKE ?)");
            }

            if (!age.equals("Any")) {
                sqlBuilder.append(" AND publisher_age_group = ?");
            }

            if (sciFiSelected || dramaSelected || novelSelected) {
                sqlBuilder.append(" AND (");

                if (sciFiSelected) {
                    sqlBuilder.append("book_type = 'Sci-fi' OR ");
                }

                if (dramaSelected) {
                    sqlBuilder.append("book_type = 'Drama' OR ");
                }

                if (novelSelected) {
                    sqlBuilder.append("book_type = 'Novel' OR ");
                }

                sqlBuilder.delete(sqlBuilder.length() - 4, sqlBuilder.length());
                sqlBuilder.append(")");
            }

            // Calculate the offset for pagination
            int offset = (currentPage - 1) * 10; // Assuming 10 records per page

            // Add the pagination clauses to the SQL query
            sqlBuilder.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT 10 ROWS ONLY");

            // Execute the SQL query
            PreparedStatement statement = connection.prepareStatement(sqlBuilder.toString());
            paramIndex = 1;

            if (!keyword.isEmpty()) {
                String keywordPattern = "%" + keyword + "%";
                statement.setString(paramIndex++, keywordPattern);
                statement.setString(paramIndex++, keywordPattern);
            }

            if (!age.equals("Any")) {
                statement.setString(paramIndex++, age);
            }

            ResultSet resultSet = statement.executeQuery();

            // Retrieve the search results
            List<Object[]> searchResults = new ArrayList<>();
            int i=0;
            while (resultSet.next()) {
                int sl = i;
                String bookName = resultSet.getString("book_name");
                
                                String publisherName = resultSet.getString("publisher_name");
                Date publishDate = resultSet.getDate("publish_date");

                Object[] rowData = {sl, bookName, publisherName, publishDate};
                searchResults.add(rowData);
                i++;
            }

            resultSet.close();
            statement.close();
            connection.close();

            // Update the table model with the search results
            for (Object[] rowData : searchResults) {
                tableModel.addRow(rowData);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while performing the search.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BookSearchSystem();
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

               

