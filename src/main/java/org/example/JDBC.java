package org.example;

import java.sql.*;

public class JDBC {
    public static ResultSet connect(String query, boolean isSelect) throws SQLException {
        ResultSet resultSet = null;
        Connection connection = null;
        Statement statement = null;

        try {
            String url = "jdbc:mysql://localhost:6969/dbdiscord";
            connection = DriverManager.getConnection(url, "root", "h2l3g1idontknow11"); // Establishing a connection to the DB
            statement = connection.createStatement(); // Usage of a communication tool to manipulate the db

            if (isSelect) {
                resultSet = statement.executeQuery(query); // Storing the record of the query which was executed with statement
            } else {
                statement.executeUpdate(query); // Have to use this if it is not a SELECT because you do not want a resultSet
            }
        } catch (SQLException e) {
            e.printStackTrace(); //Handling exceptions
        }

        return resultSet; // Does not return NULL
    }
}
