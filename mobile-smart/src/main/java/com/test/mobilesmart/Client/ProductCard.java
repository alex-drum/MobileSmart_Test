package com.test.mobilesmart.Client;

/**
 * Класс ProductCard используется для отображения всех товаров документа
 * в таблице класса BrowseSceneController
 */

public class ProductCard {
    private final String productName;
    private String productQuantity;

    public ProductCard(String productName, String productDescription, String productQuantity) {
        this.productName = productName + ", " + productDescription;
        this.productQuantity = productQuantity;
    }

    public ProductCard() {
        this.productName = "";
    }

    public String getProductName() {
        return productName;
    }

    public void setProductQuantity(String productQuantity) {
        this.productQuantity = productQuantity;
    }
    public String getProductQuantity() {
        return productQuantity;
    }

}

