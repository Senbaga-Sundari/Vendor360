package com.vendor360.database;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void testDatabaseConnection() throws Exception {

        try (Connection connection = dataSource.getConnection()) {

            assertNotNull(connection);

            System.out.println(
                "Connected to database: "
                + connection.getCatalog()
            );
        }
    }
}
