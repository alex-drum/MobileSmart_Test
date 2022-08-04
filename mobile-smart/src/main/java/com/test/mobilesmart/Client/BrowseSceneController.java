package com.test.mobilesmart.Client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

public class BrowseSceneController implements Initializable {
    public TableColumn<ProductCard, String> nameColumn;
    public TableColumn<ProductCard, String> qtyColumn;
    public TableView<ProductCard> productTable;

    String documentPath = "tmpFiles/Document.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            productTable.setOnKeyPressed( event -> {
                if (event.getCode() == KeyCode.ESCAPE ) {
                    try {
                        switchToMenu(event);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            String jsonString = Files.readAllLines(Path.of(documentPath)).toString();
            jsonString = jsonString.replace("},", "};");
            jsonString = jsonString.replace("[", "");
            jsonString = jsonString.replace("]", "");
            String[] productsString = jsonString.split(";");
            System.out.println(Arrays.asList(productsString));

            ObservableList<ProductCard> productCards = FXCollections.observableArrayList();
            for (int i = 0; i < productsString.length; i++) {
                JsonObject productJson = new JsonParser().parse(productsString[i]).getAsJsonObject();
                String name = productJson.get("product_name").getAsString();
                String description = productJson.get("product_description").getAsString();
                String qty = productJson.get("product_quantity").getAsString();

                productCards.add(new ProductCard(name, description, qty));
            }
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
            qtyColumn.setCellValueFactory(new PropertyValueFactory<>("productQuantity"));
            productTable.setItems(productCards);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchToMenu(KeyEvent actionEvent) {
        try {
            Parent newView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MenuScene.fxml")));
            Scene scene = new Scene(newView);
            Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
