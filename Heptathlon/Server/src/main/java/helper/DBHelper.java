package helper;

import java.sql.*;

/**
 * DBHelper class is used to connect to the database and execute queries.
 */
public class DBHelper {
    /**
     * Database connection parameters.
     * Only for heptathlon database.
     */
    private static final String db_name = "heptathlon";
    private static final String db_user = "user";
    private static final String db_password = "secret";
    private static final int db_port = 3310;
    private static final String db_host = "localhost";

    /**
     * Database connection URL.
     */
    private static final String db_url = "jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name + "?useSSL=false";

    /**
     * Connection object.
     */
    private Connection connection;

    /**
     * Constructor to connect to the database.
     */
    public DBHelper() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(db_url, db_user, db_password);

            if (connection != null) {
                System.out.println("Connected to the database");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to execute a query.
     * @param query SQL query.
     * @return ResultSet object.
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
     * Method to execute an update query.
     * @param query SQL query.
     * @return Number of rows affected.
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
