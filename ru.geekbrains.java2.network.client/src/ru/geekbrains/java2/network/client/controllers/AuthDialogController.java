package ru.geekbrains.java2.network.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import ru.geekbrains.java2.network.client.NetworkChatClient;
import ru.geekbrains.java2.network.client.models.Network;

public class AuthDialogController {
    private @FXML TextField loginField;
    private @FXML PasswordField passwordField;
    private @FXML Button authButton;

    private Network network;
    private NetworkChatClient clientApp;

    @FXML
    public void executeAuth(ActionEvent actionEvent) {
        String login = loginField.getText();
        String password = passwordField.getText();
        if (login == null || login.isBlank() || password == null || password.isBlank()) {
            NetworkChatClient.showNetworkError("Username and password should be not empty!", "Auth error");
            return;
        }

        String authError = network.sendAuthCommand(login, password);
        if (authError == null) {
            clientApp.openChat();
        } else {
            NetworkChatClient.showNetworkError(authError, "Auth error");
        }


    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setClientApp(NetworkChatClient clientApp) {
        this.clientApp = clientApp;
    }
}
