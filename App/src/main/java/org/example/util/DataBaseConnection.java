package org.example.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataBaseConnection {

    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/db_bisd";
        List list = new ArrayList();
        list.add("Hello");
        String text = list.get(0) + ", world!";
        System.out.print(text);
        return DriverManager.getConnection(url);
    }
}

