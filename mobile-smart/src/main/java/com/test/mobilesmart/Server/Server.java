package com.test.mobilesmart.Server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.ArrayList;

public class Server {
    private final int PORT = 8189;

    private static final String url = "jdbc:mysql://localhost/products";
    private static final String dbUser = "root";
    private static final String dbPassword = "haizi2011";

    private static Connection connection;
    private static Statement statement;
    private static ResultSet resultSet;

//    public ProductCard productCard;


    public Server() {
//        productCard = new ProductCard();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started.");

            while (true) {
                System.out.println("Server is waiting for connection.");
                        Socket socket = server.accept();
                        Handler client = new Handler(socket, this);
                        new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*   private static JSONArray getJSONArray(String query) {
        JSONArray jsonArray = new JSONArray();

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
            int i = 0;
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                JSONObject jsonObject = new JSONObject();
                for (int j = 1; j < columnCount + 1; j++) {
                    String key = rsmd.getColumnLabel(j);
                    jsonObject.put(key, resultSet.getObject(key));
                }
                jsonArray.put(i, jsonObject);
                i++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }*/

/*    public JSONArray getAllProducts() {
        JSONArray productsJSON = new JSONArray();
        String query = "SELECT product_name, "
                + "product_description, product_quantity " +
                " FROM products.stock";
        productsJSON = getJSONArray(query);
        return productsJSON;
    }*/

    public String getProductName(String barcode) {
        String productName = null;
        String query = "SELECT product_name FROM products.product_list WHERE (`barcode` = '" + barcode + "')";
        System.out.println(query);
        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword );
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    productName = resultSet.getString("product_name");
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productName;
    }

    public void createProductCard(String name, String description, String qty) throws IOException {
        String query = "INSERT INTO `products`.`stock` (`product_name`, `product_description`, `product_quantity`) VALUES ('"
                + name + "', '" + description + "', '" + qty + "')";
        insertQuery(query);
    }

    private void insertQuery(String query) {

        try {
            connection = DriverManager.getConnection(url, dbUser, dbPassword);
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            if (i == 1) {
                System.out.println("New product card is successfully created!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

