package balloonHarbourServer.main;

import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.cryptography.encryptionmethods.*;
import balloonHarbourServer.db.dbManager;
import balloonHarbourServer.networking.Server;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;

import java.io.File;
import java.math.BigInteger;
import java.sql.ResultSet;
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
        String test = "hello abcdef ABCDEF";

        EncryptionMethod enc_method = new secp256k1();
        ECC ecc = new ECC(enc_method);

        Server s = new Server(3000, ecc);
        s.start();

        //System.out.println("86708036803369872034570412862609083475515593688230928819555281574865911658929: " + new BigInteger("86708036803369872034570412862609083475515593688230928819555281574865911658929", 10).toString(2));
        //System.out.println("85477233325812616354036193957635600561136467237707362460828001779519353589532: " + new BigInteger("85477233325812616354036193957635600561136467237707362460828001779519353589532", 10).toString(2));

        //System.out.println("huso");
        //Hash sha256 = new SHA256();
        //System.out.println(sha256.hash("huso"));

        //System.out.println("\n\n");

        /*EncryptionMethod enc_method = new secp256r1();
        ECC ecc1 = new ECC(enc_method);
        ECC ecc2 = new ECC(enc_method);

        BigInteger[] s1 = ecc1.genKeys();
        BigInteger[] pub_key_1 = new BigInteger[]{s1[1], s1[2]};

        for (BigInteger b : s1) {
            System.out.println(b.toString(10));
        }

        System.out.println("\n\n");

        BigInteger[] s2 = ecc2.genKeys();
        BigInteger[] pub_key_2 = new BigInteger[]{s2[1], s2[2]};

        for (BigInteger b : s2) {
            System.out.println(b.toString(10));
        }

        System.out.println("\n\n");

        for (BigInteger b : ecc1.point_mult(s1[0], pub_key_2)) {
            System.out.println(b.toString(10));
        }

        System.out.println("\n");

        for (BigInteger b : ecc2.point_mult(s2[0], pub_key_1)) {
            System.out.println(b.toString(10));
        }*/

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

    public static void ResgisterNewUser(String username, String password) {
        try {
            user_db.getStatement().executeUpdate("INSERT INTO Users (username, password) VALUES ('" + username + "', '" + password + "')");
        } catch (SQLException e) {
            System.out.printf(e.getMessage());
        }
    }

    public static int CheckLogin(String username, String password) {
        try {
            ResultSet rs = user_db.getStatement().executeQuery("SELECT password FROM Users WHERE username = '" + username + "'");
            if (password.equals(rs.getString("password"))) {
                return 1;
            }
            return 0;
        } catch (SQLException e) {
            //e.printStackTrace();
            return 2;
        }
    }

    private static void Setup() {
        try {
            //user_db.getStatement().executeUpdate("CREATE TABLE Users (username String, password String, color String);");
            user_db.getStatement().executeUpdate("CREATE TABLE Users (username String, password String);");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}