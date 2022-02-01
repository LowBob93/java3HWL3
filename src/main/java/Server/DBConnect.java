package Server;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static Connection connection;


    public static Connection getConnection() throws SQLException {
        if(connection == null){
            connection = DriverManager.getConnection("jdbc:sqlite:MyDB.db");
        }
        return connection;
    }
}