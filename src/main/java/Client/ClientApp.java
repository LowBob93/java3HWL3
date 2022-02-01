package Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.stream.Collectors;
import java.util.List;

public class ClientApp extends JFrame {
    private final String SERVER_ADDRESS = "127.0.0.1";
    private final int SERVER_PORT = 8080;
    private String login;

    private JTextField msgInputField;
    private JTextArea chatArea;

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private LoginFrame loginFrame;
    private File storyFile;

    public ClientApp() {
        prepareGUI();
        try {
            openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void openConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());
        Thread thread = new Thread(() -> {
            boolean isItConnect = false;
            try {
                while (true) {
                    String strFromServer = dis.readUTF();
                    if (strFromServer.startsWith("/start")) {
                        loginFrame.setVisible(false);
                        this.setVisible(true);
                        String nickName = strFromServer.split(" ", 2)[1];
                        chatArea.append(nickName+ " вошел в чат " + "\n");
                        setTitle("Ваш никнейм: " + nickName);
                        storyFile = new File(login + ".txt");
                        loadStory();
                        isItConnect = true;
                        break;
                    } else if (strFromServer.startsWith("/finish")) {
                        JOptionPane.showMessageDialog(loginFrame, "Соедниение с сервером окночено");
                        closeConnection();
                        break;
                    } else {
                        loginFrame.statusLabel.setText(strFromServer);
                    }
                }
                while (isItConnect) {
                    String strFromServer = dis.readUTF();
                    String storyMessage;
                    if (strFromServer.equalsIgnoreCase("/finish")) {
                        dos.writeUTF("/finish");
                        closeConnection();
                        break;
                    } else if (strFromServer.startsWith("/w")) {
                        String[] privateParse = strFromServer.split(" ", 3);
                        storyMessage = privateParse[1] + " > Me: " + privateParse[2] + "\n";
                    } else {
                        storyMessage = strFromServer + "\n";
                    }
                    chatArea.append(storyMessage);
                    appendStory(storyMessage);
                }
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void closeConnection() {
        try {
            dis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void sendMessage() {
        String msg = msgInputField.getText();
        String storyMessage;
        if (msg != null && !msg.trim().isEmpty()) {
            try {
                dos.writeUTF(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (msg.startsWith("/w")) {
                String[] privateParse = msg.split(" ", 3);
                storyMessage = "Me > " + privateParse[1] + ": " + privateParse[2] + "\n";
            } else {
                storyMessage = "Me: " + msg + "\n";
            }
            chatArea.append(storyMessage);
            appendStory(storyMessage);
            msgInputField.setText("");
            msgInputField.grabFocus();
        }
    }

    public void prepareGUI() { //параметры окна из методички

        setBounds(600, 300, 500, 500);
        setTitle("Chinese WhatsUp");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton btnSendMsg = new JButton("Отправить");
        bottomPanel.add(btnSendMsg, BorderLayout.EAST);
        msgInputField = new JTextField();
        add(bottomPanel, BorderLayout.SOUTH);
        bottomPanel.add(msgInputField, BorderLayout.CENTER);
        btnSendMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        msgInputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    dos.writeUTF("/finish");
                } catch (IOException exc) {
                    exc.printStackTrace();
                }

            }
        });
        setVisible(false);
        loginFrame = new LoginFrame();
    }

    private void appendStory(String message) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(storyFile, true));) {
            bw.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadStory() {
        if (!storyFile.exists()) {
            try {
                storyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedReader br = new BufferedReader(new FileReader(storyFile));) {
            List<String> history = br.lines().collect(Collectors.toList());
            int skip = history.size() > 100 ? history.size() - 100 : 0;
            history.stream()
                    .skip(skip)
                    .forEach(s -> chatArea.append(s + "\n"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class LoginFrame extends JFrame {
        JLabel statusLabel = new JLabel();
        JLabel login = new JLabel("Логин");
        JLabel password = new JLabel("Пароль");
        JTextField loginField = new JTextField();
        JTextField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Войти");

        public LoginFrame() {
            setBounds(600, 300, 400, 300);
            setTitle("Chinese WhatsUp");
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            setLayout(null);
            setResizable(false);
            JPanel panel = new JPanel();
            panel.setBounds(0, 0, 500, 400);
            panel.setLayout(null);
            statusLabel.setBounds(10, 10, 290, 30);
            login.setBounds(60, 50, 290, 30);
            password.setBounds(60, 80, 290, 30);
            loginField.setBounds(120, 50, 190, 30);
            passwordField.setBounds(120, 80, 190, 30);
            loginButton.setBounds(140, 150, 100, 40);
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendLoginAndPassword();
                }
            });

            loginField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendLoginAndPassword();
                }
            });

            passwordField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sendLoginAndPassword();
                }
            });

            panel.add(statusLabel);
            panel.add(login);
            panel.add(password);
            panel.add(loginField);
            panel.add(passwordField);
            panel.add(loginButton);

            add(panel);
            setVisible(true);
        }

        public void sendLoginAndPassword() {
            String message = "/start-" + loginField.getText() + "-" + passwordField.getText();
            ClientApp.this.login = loginField.getText();
            try {
                ClientApp.this.dos.writeUTF(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}