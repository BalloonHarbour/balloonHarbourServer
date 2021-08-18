package balloonHarbourServer.main;

import balloonHarbourServer.db.dbManager;
import balloonHarbourServer.cryptography.RSA;
import balloonHarbourServer.cryptography.RSACredentials;

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

        RSA rsa = new RSA(512);

        RSACredentials credentials = rsa.createCredentials();

        BigInteger toencrypt = rsa.StringToCipher(test);
        BigInteger encrypted = rsa.encrypt(toencrypt, credentials);

        BigInteger todecipher = rsa.decrypt(encrypted, credentials);

        System.out.println(test + ": " + toencrypt);
        System.out.println(rsa.CipherToString(encrypted) + ": " + encrypted);
        System.out.println(rsa.CipherToString(todecipher) + ": " + todecipher);


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