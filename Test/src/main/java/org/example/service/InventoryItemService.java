package org.example.service;

import org.example.model.InventoryItem;
import org.example.repository.InventoryItemRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InventoryItemService {
    private InventoryItemRepository repository;

    public InventoryItemService(){
        repository = new InventoryItemRepository();
    }

    public List<InventoryItem> findAll() throws SQLException {
        return repository.findAll();
    }

    public InventoryItem findById(int id) throws SQLException {
        return repository.findById(id);
    }

}