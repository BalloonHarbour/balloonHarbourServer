package balloonHarbourServer.users;

import balloonHarbourServer.cryptography.Base64;
import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.cryptography.aes.Aes;
import balloonHarbourServer.cryptography.hashes.SHA256;
import balloonHarbourServer.main.main;
import balloonHarbourServer.networking.Server;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class User implements Runnable {

    public String username, color, enc_key;
    private Socket s;
    private Thread t;
    private boolean isKeys = true;
    private BigInteger[] keys;
    private BigInteger[] shared_secret;
    private OutputStreamWriter writer;
    private boolean isLoggedIn = false;
    private boolean isRegisteredYet = true;
    private boolean isUpdated = false;

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
                enc_key = SHA256.hash(shared_secret[0].toString(16));
            } catch (Exception e) {

            }
            //System.out.println(msg);
            msg = new String(Aes.decryptText(Base64.decode(msg), enc_key, true), StandardCharsets.UTF_8);
            //System.out.println(msg);
            //System.out.println(msg + "\n" + enc_key + " (" + shared_secret[0].toString(16) + ")"); //Testing                    ------ Test

            if (!isLoggedIn && isRegisteredYet) {
                try {
                    String username = msg.substring(0, 25).replaceAll("\\s+$", "");
                    String password = msg.substring(25);

                    switch (main.CheckLogin(username, password)) {
                        case 1:
                            System.out.println("[+] Logged in");
                            Write("sc");

                            List<String> msgs = main.getMessagesFromUser(username);
                            if (msgs != null) {
                                for (String s : msgs) {
                                    Write("ms" + s);
                                    //System.out.println(s);
                                }
                            }
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
                            main.SaveMessage(username, message, this.username);
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
                enc_key = SHA256.hash(shared_secret[0].toString(16));
                msg = Base64.encode(Aes.encryptText(msg.getBytes(StandardCharsets.UTF_8), enc_key, true));
            }
            char[] chars = msg.toCharArray();
            writer.write(chars, 0, chars.length);
            writer.flush();
        } catch (Exception e) {
            Disconnect();
        }
    }

    public void Disconnect()
    {
        try {
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

    public void UpdateMessages() {
        List<String> msgs = main.getMessagesFromUser(username);
        if (msgs != null) {
            for (String msg : msgs) {
                Write("ms" + msg);
                System.out.println(msg);
            }
        }
    }
}