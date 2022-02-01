package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    MyServer myServer;
    Socket socket;

    DataInputStream dis;
    DataOutputStream dos;

    String nickName;
    boolean isAuth = false;


    public ClientHandler(MyServer myServer, Socket socket) {
        try {
            this.socket = socket;
            this.myServer = myServer;
            this.dis = new DataInputStream(this.socket.getInputStream());
            this.dos = new DataOutputStream(this.socket.getOutputStream());

            new Thread(() -> {
                try {
                    auth();
                    if(isAuth) {
                        receiveMessage();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    closeConnections();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void sendMessage(String message) {
        try {
            dos.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeConnections() {
        myServer.unapproved(this);
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

    private void auth() throws Exception {
        long startAuthTime = System.currentTimeMillis();
        while (true) {
            if((System.currentTimeMillis() - startAuthTime) > 120000){
                dos.writeUTF("/finish");
                break;
            }
            String message = dis.readUTF();
            if (message.startsWith("/start")) {
                String[] parse = message.split("-", 3);
                if (parse.length != 3) {
                    throw new IllegalAccessException();
                }
                String nickName = myServer
                        .getAuthService()
                        .getNickNameByLoginAndPassword(parse[1].trim(), parse[2].trim());
                if (nickName != null) {
                    if (!myServer.nickNameIsBusy(nickName)) {
                        this.nickName = nickName;
                        sendMessage("/start " + nickName);
                        myServer.approved(this);
                        myServer.broadcastMessage("Пользователь " + this.nickName + " подключился к чату", this);
                        isAuth = true;
                        return;
                    } else {
                        sendMessage("Данные для входа уже используются");
                    }
                } else {
                    sendMessage("Неправильный логи или пароль.");
                }
            }
        }
    }

    private void receiveMessage() throws IOException {
        while (true) {
            String message = dis.readUTF();
            if (message.startsWith("/")) {
                if (message.startsWith("/finish")) {
                    sendMessage("/finish");
                    myServer.broadcastMessage(this.nickName + " вышел из чата", this);
                    return;
                } else if (message.startsWith("/w")) {
                    String[] privateParse = message.split(" ", 3);
                    String privateMessage = "/w " + this.nickName + " " + privateParse[2];
                    if (myServer.nickNameIsBusy(privateParse[1])) {
                        myServer.sendPrivateMessage(privateMessage, privateParse[1]);
                    } else {
                        sendMessage("Пользователь " + privateParse[1] + " отсутсвует в сети либо не существует");
                    }
                } else if (message.startsWith("/online")){
                    myServer.getOnline(this);
                }

            } else {
                myServer.broadcastMessage(this.nickName + ": " + message, this);
            }
        }
    }

}