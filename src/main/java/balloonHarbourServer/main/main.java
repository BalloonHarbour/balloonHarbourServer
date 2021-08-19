package balloonHarbourServer.main;

import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.cryptography.SHA256;
import balloonHarbourServer.cryptography.methods.Method;
import balloonHarbourServer.db.dbManager;

import java.io.File;
import java.math.BigInteger;
import java.sql.SQLException;
import java.sql.SQLOutput;

import balloonHarbourServer.cryptography.methods.secp256k1;

public class main {

    public static dbManager user_db;
    public static dbManager message_db;

    public static void main(String[] args) {
        File f = new File("db");

        if (!f.exists()) {
            f.mkdir();
            user_db = new dbManager("jdbc:sqlite:db\\users.db"); // "/db/test.db"
            message_db = new dbManager("jdbc:sqlite:db\\messages.db"); // "/db/messages.db"
            Setup();
        } else {
            user_db = new dbManager("jdbc:sqlite:db\\users.db"); // "/db/test.db"
            message_db = new dbManager("jdbc:sqlite:db\\messages.db"); // "/db/messages.db"
        }
        String test = "ahello abcdef ABCDEF";

        System.out.println(SHA256.hash(test));

        System.out.println("\n\n");

        Method enc_method = new secp256k1();
        ECC ecc = new ECC(enc_method);

        for (BigInteger b : ecc.genKeys()) {
            System.out.println(b);
        }

        /*try {
            user_db.getStatement().executeUpdate("INSERT INTO Users (username, password, color) VALUES ('admin', 'test', '#000000')");

            ResultSet rs = user_db.getStatement().executeQuery("SELECT * FROM Users");

            while (rs.next()) {
                System.out.println("NAME: " + rs.getString("username"));
                System.out.println("PASS: " + rs.getString("password"));
                System.out.println("COLO: " + rs.getString("color"));
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }*/
    }

    private static void Setup() {
        try {
            user_db.getStatement().executeUpdate("CREATE TABLE Users (username String, password String, color String);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}