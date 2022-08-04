package com.test.mobilesmart.Client;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BarcodeErrorSceneController implements Initializable {
    public AnchorPane barcodeErrorScene;
    public Button errorButton;

    public void onErrorButtonClick() {
        AnchorPane pane;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("BarcodeScene.fxml"));
            pane = loader.load();
            barcodeErrorScene.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        barcodeErrorScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                try {
                    onErrorButtonClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        barcodeErrorScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    onErrorButtonClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
