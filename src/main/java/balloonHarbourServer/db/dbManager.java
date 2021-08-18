package balloonHarbourServer.db;

import java.sql.*;

public class dbManager {

    Statement s;

    public dbManager(String url) {
        try {

            Connection c = DriverManager.getConnection(url);

            if (c != null) {
                s = c.createStatement();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public Statement getStatement() {
        return s;
    }
}