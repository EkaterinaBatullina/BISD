package org.example.repository;

import org.example.model.InventoryItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryItemRepository {
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    private static final String SQL_FIND_ALL = "SELECT * FROM inventory_items";

    private static final String SQL_FIND_BY_ID = "SELECT * FROM inventory_items WHERE id = ?";

    public List<InventoryItem> findAll() throws SQLException {
        List<InventoryItem> items = new ArrayList<>();
        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(SQL_FIND_ALL)) {
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    InventoryItem item = new InventoryItem();
                    item.setName(rs.getString("item_name"));
                    item.setParameters(rs.getString("parameters"));
                    item.setAge(rs.getInt("age"));
                    item.setPlacementConditions(rs.getString("placement_conditions"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public InventoryItem findById(int id) throws SQLException {
        try (PreparedStatement statement = databaseManager.getConnection().prepareStatement(SQL_FIND_BY_ID)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    InventoryItem item = new InventoryItem();
                    item.setId(rs.getInt("id"));
                    item.setName(rs.getString("item_name"));
                    item.setParameters(rs.getString("parameters"));
                    item.setAge(rs.getInt("age"));
                    item.setPlacementConditions(rs.getString("placement_conditions"));
                    return item;
                }
            }
        }
        return null;
    }


}
