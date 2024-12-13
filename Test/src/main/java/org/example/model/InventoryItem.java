package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter

public class InventoryItem {
    private int id;
    private String name;
    private String parameters;
    private int age;
    private String placementConditions;

    public InventoryItem() {}

}