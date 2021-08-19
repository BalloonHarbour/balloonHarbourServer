package balloonHarbourServer.main;

import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.cryptography.methods.Method;
import balloonHarbourServer.cryptography.methods.*;
import balloonHarbourServer.db.dbManager;

import java.io.File;
import java.math.BigInteger;
import java.sql.SQLException;

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

        //System.out.println("\n\n");

        Method enc_method = new secp521r1();
        ECC ecc1 = new ECC(enc_method);
        ECC ecc2 = new ECC(enc_method);

        BigInteger[] s1 = ecc1.genKeys();
        BigInteger[] pub_key_1 = new BigInteger[]{s1[1], s1[2]};

        for (BigInteger b : s1) {
            System.out.println(b.toString(16));
        }

        System.out.println("\n\n");

        BigInteger[] s2 = ecc2.genKeys();
        BigInteger[] pub_key_2 = new BigInteger[]{s2[1], s2[2]};

        for (BigInteger b : s2) {
            System.out.println(b.toString(16));
        }

        System.out.println("\n\n");

        for (BigInteger b : ecc1.point_mult(s1[0], pub_key_2)) {
            System.out.println(b.toString(16));
        }

        System.out.println("\n");

        for (BigInteger b : ecc2.point_mult(s2[0], pub_key_1)) {
            System.out.println(b.toString(16));
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