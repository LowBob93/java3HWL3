package Server;

public interface AuthService {
    void start();
    void stop();
    String getNickNameByLoginAndPassword(String login, String password);
}