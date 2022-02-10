package Server;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MyServer extends JFrame {


    private AuthService authService;
    private List<ClientHandler> handlerList;
    private static final Logger LOGGER = LogManager.getLogger(MyServer.class);

    private final int SERVER_PORT = 8080;
    private JTextArea chatArea;

    public MyServer() {
        prepareGUI();
        Thread clientThread = new Thread(() -> {                      // создаем новый поток
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                chatArea.append("Запуск сервера...\n");
                LOGGER.info("Запуск сервера");
                authService = new AuthServiceImpl();
                handlerList = new ArrayList<>();
                authService.start();
                while (true) {
                    chatArea.append("Ожидаем подключение клиента...\n");
                    LOGGER.info("Ожидаем подключение клиента");
                    Socket socket = serverSocket.accept();
                    chatArea.append("Клиент подключен.\n");
                    LOGGER.info("Клиент подключен");
                    new ClientHandler(this, socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                authService.stop();
            }
        });
        ExecutorService exService = Executors.newFixedThreadPool(10); // ExecutorService открываем 10 потоков для отработки клиентов
        exService.execute(clientThread); // передаем поток в ExecutorService
    }

    public synchronized boolean nickNameIsBusy(String nickName) {
        return handlerList
                .stream()
                .anyMatch(handler -> handler.getNickName().equalsIgnoreCase(nickName));
    }

    public synchronized void broadcastMessage(String message, ClientHandler author) {
        handlerList
                .stream()
                .filter(handler -> !handler.getNickName().equals(author.getNickName()))
                .forEach(handler -> handler.sendMessage(message));
    }

    public synchronized void sendPrivateMessage(String message, String recipient) {
        handlerList
                .stream()
                .filter(handler -> handler.getNickName().equals(recipient))
                .forEach(handler -> handler.sendMessage(message));
    }

    public synchronized void getOnline(ClientHandler author) {
        StringBuilder msg = new StringBuilder("Вошел в сеть:\n");
        for (ClientHandler handler : handlerList) {
            if (handler.getNickName().equals(author.getNickName())) {
                continue;
            }
            msg.append(handler.getNickName()).append("\n");
        }
        sendPrivateMessage(msg.toString(), author.getNickName());
    }


    public synchronized void approved(ClientHandler handler) {
        chatArea.append(handler.getNickName() + " Успешно подключился к серверу\n");
        LOGGER.info(handler.getNickName() + " Успешно подключился к серверу");
        handlerList.add(handler);
    }

    public synchronized void unapproved(ClientHandler handler) {
        String nickName = handler.getNickName() != null ? handler.getNickName() : "Пользователь не зарегестрирован";
        chatArea.append("Пользователь " + nickName + " отключен. \n");
        LOGGER.info("Пользователь " + nickName + " отключен.");
        handlerList.remove(handler);
    }

    public void prepareGUI() { //интерфейс из методички

        setBounds(600, 300, 500, 500);
        setTitle("Chinese WhatsUp Сервер");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);



        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                handlerList.forEach(handler -> handler.sendMessage("/finish"));
            }
        });
        setVisible(true);
    }

    public AuthService getAuthService() {
        return this.authService;
    }
}