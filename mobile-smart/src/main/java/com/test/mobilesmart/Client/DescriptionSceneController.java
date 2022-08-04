package com.test.mobilesmart.Client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

public class DescriptionSceneController implements Initializable {
    public AnchorPane descriptionScene;

    public Label getLabel() {
        return labelProduct;
    }

    public Label labelProduct;
    public TextField descriptionTextField;
    public Button enterDescriptionButton;

    String productFile = "tmpFiles/Product.txt";
    String documentFile = "tmpFiles/Document.txt";

    /** в переменную entry будет записываться номер ячейки массива строк,
     * если будет обнаружено вхождение аналогичного товара
     */
    private static int entry;

    @FXML
    public void onDescriptionButtonClick() throws Exception {

        /**
         * По нажатию кнопки получаем строку с описанием товара и
         * записываем ее во временный файл товара */

        String description = descriptionTextField.getText();
        String descriptionJSON = "\"product_description\":\"" + description + "\"},";
        Files.writeString(Path.of(productFile), descriptionJSON, StandardOpenOption.APPEND);

        /** создаем файл документа с массивом товаров,
         * кладем туда первый товар, удаляем файл с данными товара */
        String newProductString = Files.readString(Path.of(productFile));
        File document = new File(documentFile);

        if (!document.exists()) {
            document.getParentFile().mkdir();
            document.createNewFile();

            Files.writeString(Path.of(documentFile), newProductString);

            document = new File(productFile);
            if (document.delete()) {
                System.out.println("Product is deleted");
            }
            switchToBarcode();

            /** Если файл уже существует, то проверяем документ на наличие в нем
             * аналогичного товара */

        } else {
            if (isSameProduct(productFile, documentFile)) {
                System.out.println("Such product is already in document!");
                String[] updatedArray = updateArray(productFile, documentFile);
                for (int i = 0; i < updatedArray.length; i++) {
                    if (i == 0) {
                        Files.writeString(Path.of(documentFile), updatedArray[i] + ",");
                    } else {
                        Files.writeString(Path.of(documentFile), updatedArray[i] + ",", StandardOpenOption.APPEND);
                    }
                }
            } else {
                System.out.println("No such product in document");
                Files.writeString(Path.of(documentFile), newProductString,StandardOpenOption.APPEND);
                switchToBarcode();
            }
        }

        /** после записи в файл документа удаляем временный файл товара */
        document = new File(productFile);
        if (document.delete()) {
            System.out.println("Product is deleted");
        }

        switchToBarcode();
    }

    private String[] updateArray(String productFile, String documentFile) throws IOException {
        String newProductString = Files.readString(Path.of(productFile));
        newProductString = newProductString.replace("},", "}");
        JsonObject newProductJson = new JsonParser().parse(newProductString).getAsJsonObject();

        String documentString = Files.readAllLines(Path.of(documentFile)).toString();
        documentString = documentString.replace("[", "");
        documentString = documentString.replace("]", "");
        documentString = documentString.replace("},", "};");
        String[] productsString = documentString.split(";");

        int indexBegin = productsString[entry].lastIndexOf("quantity\":")+"quantity\":".length();
        int indexEnd = productsString[entry].indexOf(",\"product_description");
        int originalQuantity = Integer.parseInt(productsString[entry].substring(indexBegin, indexEnd));
        int updatedQuantity = originalQuantity
                + Integer.parseInt(newProductJson.get("product_quantity").getAsString());
        productsString[entry] = productsString[entry].replace(Integer.toString(originalQuantity),
                Integer.toString(updatedQuantity));

        return productsString;
    }

    private boolean isSameProduct(String productFile, String documentFile) throws IOException {
        String newProductString = Files.readString(Path.of(productFile));
        newProductString = newProductString.replace("},", "}");
        JsonObject newProductJson = new JsonParser().parse(newProductString).getAsJsonObject();

        String documentString = Files.readAllLines(Path.of(documentFile)).toString();
        documentString = documentString.replace("},", "};");
        documentString = documentString.replace("[", "");
        documentString = documentString.replace("]", "");
        String[] productsString = documentString.split(";");

        for (int i = 0; i < productsString.length; i++) {
            productsString[i] = productsString[i].replace(", {", "{");
            JsonObject oldProductJson = new JsonParser().parse(productsString[i]).getAsJsonObject();

            boolean isNameEquals, isDescriptionEquals;
            isNameEquals = newProductJson.get("product_name").getAsString()
                    .equals(oldProductJson.get("product_name").getAsString());
            isDescriptionEquals = newProductJson.get("product_description").getAsString()
                    .equals(oldProductJson.get("product_description").getAsString());

            if (isNameEquals && isDescriptionEquals) {
                entry = i;
                return true;
            }
        }
        return false;
    }

    private void switchToBarcode() {
        AnchorPane pane;
        try {
            final FXMLLoader loader = new FXMLLoader(getClass().getResource("BarcodeScene.fxml"));
            pane = loader.load();
            descriptionScene.getChildren().setAll(pane);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        descriptionTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    onDescriptionButtonClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
