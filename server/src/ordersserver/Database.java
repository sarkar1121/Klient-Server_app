/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ordersserver;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

/**
 * This class is used to work with the database
 *
 * @author Šárka
 */
public class Database {

    private final Connection conn;

    public Database(String url, String user, String password) throws SQLException {
        conn = DriverManager.getConnection(url, user, password);
    }

    /**
     * This method is used to add an consumption to the database
     *
     * @param name_item consumption
     * @param ordered date of order creation
     * @param amount items in consumption
     * @throws SQLException
     */
    public void addConsumption(String name_item, Date ordered, int amount) throws SQLException {
        String sql = "INSERT INTO consumption (name_item, ordered, amount, item_consumed) VALUES (?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name_item);
        stmt.setDate(2, ordered);
        stmt.setInt(3, amount);
        stmt.setNull(4, java.sql.Types.DATE);
        stmt.executeUpdate();

    }

    /**
     * This method is used for remove an consumption from the database
     *
     * @param name_item consumption
     * @param ordered date of order creation
     * @throws SQLException
     */
    public void removeConsumption(String name_item, Date ordered) throws SQLException {
        String sql = "DELETE FROM consumption WHERE name_item = ? AND ordered = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name_item);
        stmt.setDate(2, ordered);
        stmt.executeUpdate();
    }

    /**
     * This method is used to update an consumption in the database
     *
     * @param name_item consumption
     * @param ordered date of order creation
     * @param amount items in consumption
     * @param item_consumed date when item was consumed
     * @throws SQLException
     */
    public void updateConsumption(String name_item, Date ordered, int amount, Date item_consumed) throws SQLException {
        System.out.println("SS");
        String sql = "UPDATE consumption SET amount = ?, item_consumed = ? WHERE name_item = ? AND ordered = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, amount);
        if (item_consumed == null) {
            stmt.setNull(2, java.sql.Types.DATE);
        } else {
            stmt.setDate(2, item_consumed);
        }
        stmt.setString(3, name_item);
        stmt.setDate(4, ordered);
        stmt.executeUpdate();
    }

    /**
     * This method returns a list of items based on a date
     *
     * @param ordered date of order creation
     * @return List of string representations of consumpitons
     * @throws SQLException
     */
    public List<String> loadConsumptionsInDate(Date ordered) throws SQLException {
        String sql = "SELECT * FROM consumption WHERE ordered = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setDate(1, ordered);
        ResultSet rs = stmt.executeQuery();
        return createListOfConsumptions(rs);
    }

    /**
     * This method returns a list of items based on a year
     *
     * @param year year of order creation
     * @return Listof string representations of consumpitons
     * @throws SQLException
     */
    public List<String> loadConsumptionsInYear(int year) throws SQLException {
        String sql = "SELECT * FROM consumption WHERE YEAR(ordered) = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, year);
        ResultSet rs = stmt.executeQuery();
        return createListOfConsumptions(rs);
    }

    /**
     * This method returns a list of items based on a item
     *
     * @param name_item
     * @return List of string representations of consumpitons
     * @throws SQLException
     */
    public List<String> loadAllConsumptionsForSameItem(String name_item) throws SQLException {
        String sql = "SELECT * FROM consumption WHERE name_item = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name_item);
        ResultSet rs = stmt.executeQuery();
        return createListOfConsumptions(rs);
    }

    /**
     * This method creates a list string representations of consumptions
     *
     * @param rs is resulset obtained from database
     * @return List of string representations of consumpitons
     * @throws SQLException
     */
    public List<String> createListOfConsumptions(ResultSet rs) throws SQLException {
        List<String> consumptions = new ArrayList<>();
        while (rs.next()) {
            consumptions.add(rs.getString("name_item") + "|" + rs.getString("ordered") + "| " + rs.getString("amount") + "| " + rs.getString("item_consumed"));
        }
        return consumptions;
    }

    /**
     * This method loads all Items from database
     *
     * @return List of string representations of items
     * @throws SQLException
     */
    public List<String> loadAllItems() throws SQLException {
        String sql = "SELECT * FROM item";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        return createListOfItems(rs);
    }

    /**
     * This method returns list of strings which represents items This method is
     * prepare for future extension this class
     *
     * @param rs is resul set from database
     * @return List of string representations of items
     * @throws SQLException
     */
    public List<String> createListOfItems(ResultSet rs) throws SQLException {
        List<String> items = new ArrayList<>();
        while (rs.next()) {
            items.add(rs.getString("name") + "| " + rs.getString("category"));
        }
        return items;
    }

    /**
     * This method is used to add the item to the database
     *
     * @param name name of the item
     * @param category category of the item
     * @throws SQLException
     */
    public void addItem(String name, String category) throws SQLException {
        String sql = "INSERT INTO item (name,  category) VALUES ( ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.setString(2, category);
        stmt.executeUpdate();
    }

    /**
     * This method is used for remove the Item from the database
     *
     * @param name name of the item
     * @throws SQLException
     */
    public void removeItem(String name) throws SQLException {
        String sql = "DELETE FROM item WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, name);
        stmt.executeUpdate();
    }

    /**
     * This method is used for update the Item in the database
     *
     * @param name name of the item
     * @param category category of the item
     * @throws SQLException
     */
    public void updateItem(String name, String category) throws SQLException {
        String sql = "UPDATE item SET  category = ? WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, category);
        stmt.setString(2, name);
        stmt.executeUpdate();
    }

}
