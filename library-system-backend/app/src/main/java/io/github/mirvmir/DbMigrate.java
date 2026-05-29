package io.github.mirvmir;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

public class DbMigrate {
    public static void migrate() throws Exception {
        String url = "jdbc:postgresql://localhost:5433/bookstore_db";
        String user = "postgres";
        String pass = "psql_password";

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            Database db = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/changelog.sql",
                    new ClassLoaderResourceAccessor(),
                    db
            );

            liquibase.update();
        }
    }
}
