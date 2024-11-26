package org.example;

import org.flywaydb.core.Flyway;

public class TestMigration {
    public static void main(String[] args) {
        // Create the Flyway instance and point it to the database
        Flyway flyway = Flyway.configure()//.baselineOnMigrate(true)
                .dataSource("jdbc:postgresql://localhost:5432/lab06", "postgres", "post").load();

        // Start the migration
        flyway.migrate();

    }
}