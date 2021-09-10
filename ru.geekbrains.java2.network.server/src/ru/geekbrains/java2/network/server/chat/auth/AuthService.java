package ru.geekbrains.java2.network.server.chat.auth;

public interface AuthService {

    void start();

    String getUsernameByLoginAndPassword(String login, String password);

    void stop();

}
