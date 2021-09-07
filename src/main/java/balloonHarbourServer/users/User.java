package balloonHarbourServer.users;

import balloonHarbourServer.cryptography.AES;
import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.cryptography.hashes.Hash;
import balloonHarbourServer.cryptography.hashes.SHA256;
import balloonHarbourServer.main.main;
import balloonHarbourServer.networking.Server;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class User implements Runnable {

    public String username, color;
    private Socket s;
    private Thread t;
    private boolean isKeys = true;
    private BigInteger[] keys;
    private BigInteger[] shared_secret;
    private OutputStreamWriter writer;
    private byte[] enc_key;
    private boolean isLoggedIn = false;
    private boolean isRegisteredYet = true;

    public User(Socket s, BigInteger[] keys) {
        this.s = s;
        this.keys = keys;

        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            writer = new OutputStreamWriter(s.getOutputStream(), Charset.forName("UTF-8").newEncoder());
            Write(keys[1].toString(16) + "/" + keys[2].toString(16));
            int chr;

            while (!s.isClosed()) {
                if ((chr = s.getInputStream().read()) == -1) {
                    System.out.println("[*] Client[" + getIP() + "] disconnected");
                    Disconnect();
                }

                if (s.getInputStream().available() > 0) {
                    byte[] buffer = new byte[s.getInputStream().available()];

                    for (int i = 0; i <= s.getInputStream().available(); i++) {
                        s.getInputStream().read(buffer);
                    }
                    InterpretMessage((char) chr + new String(buffer));
                }
            }
        } catch (IOException e) {
            Disconnect();
        }
    }

    private void InterpretMessage(String msg) {
        if (!isKeys) {
            try {
                enc_key = SHA256.getSHA(shared_secret[0].toString(16));
                //System.out.println("enc_key: " + SHA256.hash(shared_secret[0].toString(16)) + " (" + shared_secret[0].toString(16) + ")");
            } catch (Exception e) {

            }
            //System.out.println("shared secret: " + enc_key);
            //msg = AES.decrypt(msg, enc_key);                      ------ Test
            //System.out.println(msg); //Testing                    ------ Test
            //Write(msg);                                           ------ Test

            if (!isLoggedIn && isRegisteredYet) {
                try {
                    String username = msg.substring(0, 25).replaceAll("\\s+$", "");
                    String password = msg.substring(25);
                    //System.out.println("username: " + username + "\npassword: " + password);

                    switch (main.CheckLogin(username, password)) {
                        case 1:
                            System.out.println("[+] Logged in");
                            Write("sc");
                            this.username = username;
                            isLoggedIn = true;
                            break;

                        case 0:
                            System.out.println("[-] False Password");
                            Write("wp");
                            break;

                        case 2:
                            System.out.println("[*] Not Registered yet");
                            Write("na");
                            isRegisteredYet = false;
                            break;
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    //System.out.println("login failed");
                    Disconnect();
                }
            } else if (!isRegisteredYet) {
                String username = msg.substring(0, 25).replaceAll("\\s+$", "");
                String password = msg.substring(25);
                main.ResgisterNewUser(username, password);
                System.out.println("[*] Registered new user");
                Write("rg");
                isRegisteredYet = true;
            }

            if (msg.length() > 1) {
                switch (msg.substring(0, 2)) {
                    case "ms":
                        String username = msg.substring(2, 27).replaceAll("\\s+$", "");
                        String message = msg.substring(27);

                        if (main.CheckLogin(username, "") == 2) {
                            Write("pr" + "Given Receiver is not registered on this server!");
                            break;
                        }

                        User u;
                        if ((u = Server.getUserByName(username)) != null) {
                            u.Write("ms" + padRight(this.username, 25) + message);
                        } else {
                            System.out.println(username + " is not online to receive message: " + message);
                        }
                        break;

                    case "lo":
                        break;

                    case "dc":
                        break;
                }
            }
        } else {
            BigInteger[] pub_key = new BigInteger[]{new BigInteger(msg.split("/")[0], 16), new BigInteger(msg.split("/")[1], 16)};
            shared_secret = ECC.point_mult(keys[0], pub_key);
            isKeys = false;
        }
    }

    public void Write(String msg) {
        try {
            if (!isKeys) {
                enc_key = SHA256.getSHA(shared_secret[0].toString(16));
                //System.out.println("enc_key: " + SHA256.hash(shared_secret[0].toString(16)));
                //System.out.println("shared secret: " + enc_key);
                //msg = AES.encrypt(msg, enc_key);                  ------ Test
                //System.out.println("msg: " + msg);
            }
            char[] chars = msg.toCharArray();
            writer.write(chars, 0, chars.length);
            writer.flush();
            /*byte[] chars = msg.getBytes(StandardCharsets.US_ASCII);
            s.getOutputStream().write(chars, 0, chars.length);
            s.getOutputStream().flush();*/
        } catch (Exception e) {
            Disconnect();
        }
    }

    public void Disconnect()
    {
        try {
            //s.getInputStream().close();
            //s.getOutputStream().close();
            writer.close();
            s.close();
            Server.onlineUsers.remove(this);
        } catch (IOException e) {

        }
    }

    public String getIP() {
        return ((InetSocketAddress) s.getRemoteSocketAddress()).getHostString();
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}