package com.test.mobilesmart.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class BarcodeSceneController implements Initializable {
    @FXML
    public TextField barcodeTextField;
    @FXML
    public Button enterBarcodeButton;
    @FXML
    public AnchorPane barcodeScene;

    private static Socket socket;
    private static DataInputStream is;
    private static DataOutputStream os;

    private final int PORT = 8189;

    String productFile = "tmpFiles/Product.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        barcodeScene.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ESCAPE ) {
                try {
                    switchToMenu();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );

        barcodeTextField.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                try {
                    onBarcodeButtonClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } );
    }

    private void switchToMenu() {
        AnchorPane pane;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuScene.fxml"));
            pane = loader.load();
            barcodeScene.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    protected void onBarcodeButtonClick() throws Exception {
        /**
         * Отправляем запрос на сервер для получения соответствующего
         * наименования товара по его щтрих-коду
         */
        socket = new Socket("localhost", PORT);
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());

        barcodeTextField.setOnAction(this::sendMessage);
        String barcode = barcodeTextField.getText();

        os.writeUTF("/getProductName");
        os.writeUTF(barcode);

        String message = is.readUTF();
        closeConnection();

        /**
         * Проверяем, есть ли товар с запрашиваемым штрихкодом.
         * Если нет такого товара - уходим на окно с ошибкой.
         * Если есть такой товар - полученное наименование записываем
         * во временный файл product.txt
         */

        if (message.equals("/noSuchBarcode")) {
            AnchorPane pane;
            try {
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("BarcodeErrorScene.fxml"));
                pane = loader.load();
                barcodeScene.getChildren().setAll(pane);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String productName = message;
            if (message.contains("\"")) {
                productName = message.replace("\"", "\\\"");
            }
            String s = "{\"product_name\":\"" + productName + "\",";
            File file = new File(productFile);
            if (!file.exists()) {
                file.getParentFile().mkdir();
                file.createNewFile();
            }
            Files.writeString(Path.of(productFile), s, StandardOpenOption.APPEND);

            AnchorPane pane;
            try {
                final FXMLLoader loader = new FXMLLoader(getClass().getResource("QuantityScene.fxml"));
                pane = loader.load();
                barcodeScene.getChildren().setAll(pane);
                ((QuantitySceneController) loader.getController()).getLabel().setText(message);
                ((QuantitySceneController) loader.getController()).setProductName(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        }

    public void sendMessage(ActionEvent actionEvent) {
        String message = barcodeTextField.getText();
        try {
            os.writeUTF(message);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        barcodeTextField.clear();
    }

    public void closeConnection() {
        try {
            os.writeUTF("/quit");
            os.close();
            is.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
