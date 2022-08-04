package com.test.mobilesmart.Client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ResourceBundle;

public class QuantitySceneController implements Initializable {
    public Label labelProduct;
    public TextField quantityTextField;
    public Button enterQuantityButton;
    public AnchorPane quantityScene;

    private String productName;
    String productFile = "tmpFiles/Product.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        quantityTextField.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                try {
                    onQuantityButtonClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } );

    }

    @FXML
    private void onQuantityButtonClick() throws Exception {
        String quantity = quantityTextField.getText();
        AnchorPane pane;

        try {
            String s = "\"product_quantity\":" + quantity + ",";
            Files.writeString(Path.of(productFile), s, StandardOpenOption.APPEND);

            final FXMLLoader loader = new FXMLLoader(getClass().getResource("DescriptionScene.fxml"));
            pane = loader.load();
            quantityScene.getChildren().setAll(pane);
            ((DescriptionSceneController) loader.getController()).getLabel().setText(productName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final Label getLabel(){
        return labelProduct;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
}
