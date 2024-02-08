/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordersclient;

import java.io.*;
import java.net.*;

/**
 * Class for create communication with server
 * @author hrusk
 */
public class ClientCommunicationManagement implements Runnable{
    PrintWriter out;
    BufferedReader in;
    String command;
    Socket socket;


    public ClientCommunicationManagement(Socket socket, String command) throws Exception {
        String hostName = "127.0.0.1";
        int portNumber = 8000;
        this.socket = socket;
        this.in = new BufferedReader(new java.io.InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.command=command;
    }
    
    /**
     * Method for sending requests
     */
    public void sendRequest(){
        out.println(command);
    }
    
    /**
     * Method for getting messages
     * @return return getting message
     * @throws Exception 
     */
    public String getMessage() throws Exception{
        while(true){
            while(in.ready()){
                String message =in.readLine();
                socket.close();
                return message;
            }
        }
    }
    
    @Override
    public void run(){
        sendRequest();
        try{
            System.out.println(getMessage());
        }catch(Exception e){ 
            System.out.println(e);
        }
    }
}
