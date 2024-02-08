/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ordersserver;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Class that handles requests
 * @author hrusk
 */
public class RequestHandeler implements Runnable {

    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private final Database database;
    SimpleDateFormat format;

    public RequestHandeler(Socket cSocket, Database database) throws IOException {
        this.database = database;
        format = new SimpleDateFormat("yyyy-MM-dd");
        client = cSocket;
        out = new PrintWriter(client.getOutputStream());
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        client.setTcpNoDelay(true);
    }


    @Override
    public void run() {
        while (true) {
            try {
                while (in.ready()) {
                    String finalAnswer;
                    String dotaz = in.readLine();
                    System.out.println(dotaz);
                    String[] input = dotaz.split("\\|");

                    switch (input[0]) {
                        case "addCon" -> {
                            database.addConsumption(input[1], getDate(input[2]), Integer.parseInt(input[3]));
                            out.println("itemAdded");
                        }

                        case "remCon" -> {
                            database.removeConsumption(input[1], getDate((input[2])));
                            out.println("Succesfuly removed");
                        }

                        case "updCon" -> {
                            String last=null;
                            if(input.length>4){
                                last =(input[4]);
                            }
                            database.updateConsumption(input[1], getDate(input[2]), Integer.parseInt(input[3]), getDate(last));
                            out.println("Succesfuly updated");
                        }

                        case "loadConsDate" -> {
                            finalAnswer = "";
                            for (String consumtion : database.loadConsumptionsInDate(getDate(input[1]))) {
                                finalAnswer += consumtion + "/";
                            }
                            out.println(finalAnswer);
                        }

                        case "loadConsYear" -> {
                            finalAnswer = "";
                            for (String consumtion : database.loadConsumptionsInYear(Integer.parseInt(input[1]))) {
                                finalAnswer += consumtion + "/";
                            }
                            out.println(finalAnswer);
                        }

                        case "loadConsItem" -> {
                            finalAnswer = "";
                            for (String item : database.loadAllConsumptionsForSameItem(input[1])) {
                                finalAnswer += item + "/";
                            }
                            out.println(finalAnswer);
                        }

                        case "allItems" -> {
                            finalAnswer = "";
                            for (String item : database.loadAllItems()) {
                                finalAnswer += item + "/";
                            }
                            System.out.println(finalAnswer);
                            out.println(finalAnswer);
                        }

                        case "addItem" -> {
                            database.addItem(input[1], input[2]);
                            out.println("itemAdded");
                        }

                        case "remItem" -> {
                            database.removeItem(input[1]);
                            out.println("Succesfuly added");
                        }

                        case "updItem" -> {
                            database.updateItem(input[1], input[2]);
                            out.println("Succesfuly added");
                        }

                        default ->
                            System.out.println("Command cannot be executed");
                    }
                }
            } catch (NumberFormatException | SQLException | IOException | ParseException e) {
                out.println(e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (client != null && !client.isClosed()) {
                        client.close();
                    }
                } catch (IOException e) {
                    out.println(e);
                }
            }
        }

    }

    /**
     * Converts a string representation to a date
     * @param date string representation of the date
     * @return java.sql.Date of the date
     * @throws ParseException 
     */
    private java.sql.Date getDate(String date) throws ParseException {
        if(date==null){
            return null;
        }
        return new java.sql.Date(format.parse(date).getTime());
    }
}
