package ru.geekbrains.java2.network.server.chat.handler;

import ru.geekbrains.java2.network.server.chat.MyServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    private static final String AUTH_CMD_PREFIX = "/auth";
    private static final String AUTHOK_CMD_PREFIX = "/authok";
    private static final String AUTHERR_CMD_PREFIX = "/autherr";

    private final MyServer myServer;
    private final Socket clientSocket;

    private DataInputStream in;
    private DataOutputStream out;

    private String username;

    public ClientHandler(MyServer myServer, Socket clientSocket) {
        this.myServer = myServer;
        this.clientSocket = clientSocket;
    }

    public void handle() throws IOException {
        in  = new DataInputStream(clientSocket.getInputStream());
        out = new DataOutputStream(clientSocket.getOutputStream());

        new Thread(() -> {
            try {
                authentication();
                readMessages();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    closeConnection();
                } catch (IOException e) {
                    System.err.println("Failed to close connection!");
                }
            }
        }).start();
    }

    public String getUsername() {
        return username;
    }

    private void readMessages() throws IOException {
        while (true) {
            String message = in.readUTF();
            System.out.println("message from " + username + ": " + message);
            if (message.startsWith("/end")) {
                return;
            }
            myServer.broadcastMessage(message, this);
            if (message.startsWith("/w")){
                String[] words = message.split(" ");
                String usernameoftheReciever = words[1];
                String msg = message.substring(3);
                myServer.privateMsg(username + ": " + msg, usernameoftheReciever);
            } else {
                MyServer.broadcastMsg(username + ": " + message);}
        }
        }


    private void authentication() throws IOException {
        while (true) {
            String message = in.readUTF();
            if (message.startsWith(AUTH_CMD_PREFIX)) {
                String[] parts = message.split("\\s+", 3);// один пробел и более
                String login = parts[1];
                String password = parts[2];
                this.username = myServer.getAuthService().getUsernameByLoginAndPassword(login, password);
                if (username != null) {
                    if (myServer.isNicknameAlreadyBusy(username)) {
                        out.writeUTF(AUTHERR_CMD_PREFIX + " Login and password are already used!");
                    }
                    out.writeUTF(AUTHOK_CMD_PREFIX + " " + username);
                    myServer.broadcastMessage(username + " joined to chat!", this);
                    myServer.subscribe(this);
                    break;
                } else {
                    out.writeUTF(AUTHERR_CMD_PREFIX + " Login and/or password are invalid! Please, try again");
                }
            } else {
                out.writeUTF(AUTHERR_CMD_PREFIX + " /auth command is required!");
            }
        }
    }

    private void closeConnection() throws IOException {
        myServer.unsubscribe(this);
        clientSocket.close();
    }


    public void sendMessage(String message) throws IOException {
        out.writeUTF(message);
    }
}
