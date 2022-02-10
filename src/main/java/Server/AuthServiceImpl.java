package Server;

import java.sql.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AuthServiceImpl implements AuthService {

    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);
    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement preparedStatement;

    private void createTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS MyDB (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                "login TEXT NOT NULL," +
                "password TEXT NOT NULL," +
                "nickname TEXT NOT NULL" +
                ");");
    }
   /* private void insertData() throws SQLException{            //Добавляем новых пользователей
        statement.executeUpdate("INSERT INTO MyDB (login, password, nickname)\n"
                +"VALUES('admin','admin','ADMINISTRATOR');");
        statement.executeUpdate("INSERT INTO MyDB (login, password, nickname)\n"
                +"VALUES('loki','mew','Loki-cat');");
        statement.executeUpdate("INSERT INTO MyDB (login, password, nickname)\n"
                +"VALUES('anton','tony','Soprano');");

    }
*/
    @Override
    public void start() {
        try {
            connection = DBConnect.getConnection();
            statement = connection.createStatement();
            createTable();
           // insertData();
            preparedStatement = connection.prepareStatement("SELECT * FROM MyDB WHERE login = ? AND password = ?");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Ожидаем авторизации");
            LOGGER.info("Ожидаем авторизации");
        }
    }

    @Override
    public void stop() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOGGER.info("Авторизация окончена");
    }
    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
        try {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);

            ResultSet user = preparedStatement.executeQuery();
            if (user.next()) {
                LOGGER.info("Пользователь  " + login +" успешно подключен");
                return user.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOGGER.warn("Пользователь " + login + " ввел  неверный пароль");
        return null;
    }



}