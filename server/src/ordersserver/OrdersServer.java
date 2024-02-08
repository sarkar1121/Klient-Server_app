/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ordersserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main server class 
 * @author Šárka
 */
public class OrdersServer {

    public static void main(String[] args) {
        Database database;
        ServerSocket serverSocket;
        ExecutorService pool;
        
        try {
        database = new Database("jdbc:mysql://localhost:3306/orderingsystem", "root", "passwdRoot16548");
        int portNumber = 8000;

        pool = Executors.newCachedThreadPool();
        ArrayList<RequestHandeler> clients = new ArrayList<>();
        serverSocket = new ServerSocket(portNumber);
        while (true) {
            try {
                Socket cSocket = serverSocket.accept();
                RequestHandeler cThread = new RequestHandeler(cSocket, database);
                clients.add(cThread);
                pool.execute(cThread);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    } catch (Exception e) {
       System.out.println(e);
    } 
    }
}
