package com.test.mobilesmart.Server;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.test.mobilesmart.Client.ProductCard;
import org.json.JSONArray;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class Handler implements Runnable {

    private final DataInputStream is;
    private final DataOutputStream os;
    private final Server server;
    private final Socket socket;

    public Handler(Socket socket, Server server) throws IOException {
        this.server = server;
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        System.out.println("Client handled: ip = " + socket.getInetAddress());

    }

    public void sendMessage(String message) throws IOException {
        os.writeUTF(message);
        os.flush();
    }

    public void run() {
        while (true) {
            System.out.println("Handler: Waiting for message");
            try {
                String message = is.readUTF();
                System.out.println("message: " + message);
                if (message.equals("/quit")) {
                    os.close();
                    is.close();
                    System.out.println("client " + socket.getInetAddress() + " disconnected");
                    socket.close();
                    break;
                }

                if (message.equals("/getProductName")) {
                    String barcode = is.readUTF();
                    if (message.equals("/quit")) {
                        os.close();
                        is.close();
                        System.out.println("client " + socket.getInetAddress() + " disconnected");
                        socket.close();
                        break;
                    }
                    String productName = server.getProductName(barcode);
                    if (productName == null) {
                        sendMessage("/noSuchBarcode");
                        break;
                    }
                    sendMessage(productName);
//                    server.productCard.setProductName(productName);
                }

                if (message.equals("/setProduct")) {
                    String jsonString = is.readUTF();
                    jsonString = jsonString.replace("},", "};");
                    jsonString = jsonString.replace("[", "");
                    jsonString = jsonString.replace("]", "");
                    String[] productsString = jsonString.split(";");

                    for (int i = 0; i < productsString.length; i++) {
                        JsonObject productJson = new JsonParser().parse(productsString[i]).getAsJsonObject();
                        String name = productJson.get("product_name").getAsString();
                        String description = productJson.get("product_description").getAsString();
                        String qty = productJson.get("product_quantity").getAsString();
                        server.createProductCard(name, description, qty);
                    }
//                    if (description != null) {
//                        JsonObject jo = new JsonParser().parse(description).getAsJsonObject();
//                    }
//                    server.productCard.setProductDescription(description);
//                    server.createProductCard();
                }
/*                if (message.equals("/getAllProducts")) {
                    JSONArray products = server.getAllProducts();
                    os.writeUTF(products.toString());
                    System.out.println(products.toString());
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
