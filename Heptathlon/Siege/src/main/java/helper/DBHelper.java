package helper;

import java.sql.*;

/**
 * This class is used to connect to the database and execute queries.
 */
public class DBHelper {

    /**
     * Database credentials.
     */
    private static final String db_user = "user";
    private static final String db_password = "secret";
    private static final int db_port = 3310;
    private static final String db_host = "localhost";

    /**
     * Connection to the database instance.
     */
    private Connection connection;

    /**
     * Constructor to connect to the database.
     * @param databaseName name of the database.
     */
    public DBHelper(String databaseName) {
        String db_url = "jdbc:mysql://" + db_host + ":" + db_port + "/" + databaseName + "?useSSL=false";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(db_url, db_user, db_password);

            if (connection != null) {
                System.out.println("Connected to the database " + databaseName);
            } else {
                System.out.println("Failed to make connection to the database " + databaseName);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Execute a query on the database.
     * @param query SQL query to be executed.
     * @return ResultSet containing the results of the query.
     */
    public ResultSet executeQuery(String query) {
        if (connection == null) {
            System.out.println("Connection is null");
            return null;
        } else {
            try {
                Statement statement = connection.createStatement();
                return statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * Execute an update query on the database.
     * @param query SQL query to be executed.
     * @return number of rows affected by the query.
     */
    public int executeUpdateQuery(String query) {
        if (connection == null) {
            System.out.println("Connection is null");
            return 0;
        } else {
            try {
                Statement statement = connection.createStatement();
                return statement.executeUpdate(query);
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
