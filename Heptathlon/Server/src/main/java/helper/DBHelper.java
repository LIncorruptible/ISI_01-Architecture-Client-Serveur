package helper;

import java.sql.*;

public class DBHelper {

    private static final String db_name = "heptathlon";
    private static final String db_user = "user";
    private static final String db_password = "secret";
    private static final int db_port = 3310;
    private static final String db_host = "localhost";

    private static final String db_url = "jdbc:mysql://" + db_host + ":" + db_port + "/" + db_name + "?useSSL=false";

    private Connection connection;

    public DBHelper() {
        connection = createConnection();
    }

    private Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection resultConnection = DriverManager.getConnection(db_url, db_user, db_password);
            if (resultConnection != null) {
                System.out.println("Connection successful");
                return resultConnection;
            } else {
                System.out.println("Connection failed");
                return null;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null || connection.isClosed()) {
            connection.close();
            System.out.println("Connection closed");
        } else {
            System.out.println("Connection is already closed");
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            if (connection == null || connection.isClosed()) connection = createConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int executeUpdateQuery(String query) {
        try {
            if (connection == null || connection.isClosed()) connection = createConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            Statement statement = connection.createStatement();
            int result = statement.executeUpdate(query);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
