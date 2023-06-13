import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost/karmapoints";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "@euBebel10";

    private Connection connection;

    public DatabaseConnector() {
        connect();
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Conectado ao banco de dados.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão encerrada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
