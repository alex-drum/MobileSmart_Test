package com.test.mobilesmart.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class MenuSceneController {
    public Button startButton, browseButton, tempQuitButton, finishButton;
    public AnchorPane menuScene;

    private final int PORT = 8189;
    private Socket socket;
    private static DataInputStream is;
    private static DataOutputStream os;

    String documentPath = "tmpFiles/Document.txt";

    public static void stop() {
        try {
            os.writeUTF("/quit");
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onStartButtonClick() throws Exception {
        AnchorPane pane;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("BarcodeScene.fxml"));
            pane = loader.load();
            menuScene.getChildren().setAll(pane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onBrowseButtonClick(ActionEvent actionEvent) {
        try {
            Parent newView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("BrowseScene.fxml")));
            Scene scene = new Scene(newView);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onQuitButton(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }

    public void onFinishButton(ActionEvent actionEvent) throws IOException {
        try {
            socket = new Socket("localhost", PORT);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String everything = Files.readAllLines(Path.of(documentPath)).toString();
        os.writeUTF("/setProduct");
        os.writeUTF(everything);

        stop();
        switchToResult();
    }

    private void switchToResult() {
        AnchorPane pane;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultScene.fxml"));
            pane = loader.load();
            menuScene.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}