package balloonHarbourServer.users;

import balloonHarbourServer.cryptography.Base64;
import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.cryptography.aes.Aes;
import balloonHarbourServer.cryptography.hashes.SHA256;
import balloonHarbourServer.main.main;
import balloonHarbourServer.networking.Server;
import com.sun.security.jgss.GSSUtil;

import java.io.*;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class User implements Runnable {

    public String username, color;
    private Socket s;
    private Thread t;
    private boolean isKeys = true;
    private BigInteger[] keys;
    private BigInteger[] shared_secret;
    private OutputStreamWriter writer;
    private boolean isLoggedIn = false;
    private boolean isRegisteredYet = true;
    private boolean isUpdated = false;
    private byte[] enc_key;

    public User(Socket s, BigInteger[] keys) {
        this.s = s;
        this.keys = keys;

        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            //writer = new OutputStreamWriter(s.getOutputStream(), Charset.forName("UTF-8").newEncoder());
            Write(keys[1].toString(16) + "/" + keys[2].toString(16), Commands.KEY);
            //System.out.println("My PubKey = (" + keys[1] + "," + keys[2] + ")");
            //System.out.println(keys[1].toString(16) + "/" + keys[2].toString(16));
            int chr;

            while (!s.isClosed()) {
                if ((chr = s.getInputStream().read()) == -1) {
                    System.out.println("[*] Client[" + getIP() + "] disconnected");
                    Disconnect();
                    break;
                }

                //if (s.getInputStream().available() > 0) {
                    byte[] buffer = new byte[s.getInputStream().available()];

                    for (int i = 0; i <= s.getInputStream().available(); i++) {
                        s.getInputStream().read(buffer);
                    }

                    //Test
                    System.out.println(chr + ": " + (char) chr);

                    for (byte b : buffer) {
                        System.out.println((int)b + ": " + (char)b);
                    }

                    InterpretMessage((char) chr + new String(buffer));
                //}
            }
        } catch (IOException e) {
            Disconnect();
        }
    }

    private void InterpretMessage(String msg) {
        if (!isKeys) {
            try {
                enc_key = SHA256.getSHA(shared_secret[0].toString(16));
            } catch (Exception e) {

            }
            //System.out.println(msg);
            //msg = new String(Arrays.copyOfRange(msg.getBytes(StandardCharsets.UTF_8), 1, msg.getBytes(StandardCharsets.UTF_8).length), StandardCharsets.UTF_8);
            byte[] msg_bytes = msg.getBytes(StandardCharsets.UTF_8);
            byte[] post = new byte[msg_bytes.length - 1];
            System.arraycopy(msg_bytes, 1, post, 0, post.length);
            msg = new String(post);
            System.out.println("pre: " + msg);
            msg = new String(Aes.decryptText(Base64.decode(msg), enc_key), StandardCharsets.UTF_8);
            System.out.println("post: " + msg);

            //System.out.println(msg + "\n" + enc_key + " (" + shared_secret[0].toString(16) + ")"); //Testing                    ------ Test

            if (!isLoggedIn && isRegisteredYet && (int) msg.getBytes(StandardCharsets.UTF_8)[0] == Commands.LOGIN) {
                try {
                    String username = msg.substring(0, 25).replaceAll("\\s+$", "");
                    String password = msg.substring(25);

                    switch (main.CheckLogin(username, password)) {
                        case 1:
                            System.out.println("[+] Logged in");
                            Write(Commands.SUCCESSFULLY_LOGGED_IN);

                            List<String> msgs = main.getMessagesFromUser(username);
                            if (msgs != null) {
                                for (String s : msgs) {
                                    Write(s, Commands.MESSAGE);
                                    //System.out.println(s);
                                }
                            }
                            this.username = username;
                            isLoggedIn = true;
                            break;

                        case 0:
                            System.out.println("[-] False Password");
                            Write(Commands.WRONG_PASSWORD);
                            break;

                        case 2:
                            System.out.println("[*] Not Registered yet");
                            Write(Commands.NOT_REGISTERED);
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
                Write(Commands.SUCCESSFULLY_REGISTERED);
                isRegisteredYet = true;
            }

            if (msg.length() > 0) {
                switch ((int) msg.getBytes(StandardCharsets.UTF_8)[0]) {
                    case Commands.MESSAGE:
                        String username = msg.substring(2, 27).replaceAll("\\s+$", "");
                        String message = msg.substring(27);

                        if (main.CheckLogin(username, "") == 2) {
                            Write("Given Receiver is not registered on this server!", Commands.PRINT_ERROR);
                            break;
                        }

                        User u;
                        if ((u = Server.getUserByName(username)) != null) {
                            u.Write(padRight(this.username, 25) + message, Commands.MESSAGE);
                        } else {
                            main.SaveMessage(username, message, this.username);
                            System.out.println(username + " is not online to receive message: " + message);
                        }
                        break;

                    case Commands.LOGOUT:
                        break;

                    case Commands.DISCONNECT:
                        break;
                }
            }
        } else {
            msg = new String(Arrays.copyOfRange(msg.getBytes(StandardCharsets.UTF_8), 1, msg.getBytes(StandardCharsets.UTF_8).length), StandardCharsets.UTF_8);
            //System.out.println(msg);
            BigInteger[] pub_key = new BigInteger[]{new BigInteger(msg.split("/")[0], 16), new BigInteger(msg.split("/")[1], 16)};
            shared_secret = ECC.point_mult(keys[0], pub_key);
            //System.out.println("His PubKey = (" + pub_key[0] + "," + pub_key[1] + ")");
            System.out.println(shared_secret[0].toString());
            isKeys = false;
        }
    }

    public void Write(String msg, int command) {
        try {
            /*if (!isKeys) {
                enc_key = SHA256.getSHA(shared_secret[0].toString(16));
                msg = Base64.encode(Aes.encryptText(msg.getBytes(StandardCharsets.UTF_8), enc_key));
            }
            char[] chars = new char[msg.length() + 1];
            chars = Arrays.copyOfRange(msg.toCharArray(), 1, chars.length);
            chars[0] = (char) command;

            writer.write(chars, 0, chars.length);
            writer.flush();*/

            if (command != Commands.KEY) {
                enc_key = SHA256.getSHA(shared_secret[0].toString(16));
                msg = Base64.encode(Aes.encryptText(msg.getBytes(StandardCharsets.UTF_8), enc_key));
            }
            System.out.println(msg);
            byte[] msg_bytes = msg.getBytes(StandardCharsets.UTF_8);
            byte[] bytes = new byte[msg_bytes.length + 1];
            //bytes = Arrays.copyOfRange(msg.getBytes(StandardCharsets.UTF_8), 1, bytes.length);
            System.arraycopy(msg_bytes, 0, bytes, 1, msg_bytes.length);
            bytes[0] = (byte) command;

            s.getOutputStream().write(bytes, 0, bytes.length);
            s.getOutputStream().flush();
        } catch (Exception e) {
            //e.printStackTrace();
            Disconnect();
        }
    }

    public void Write(int command) {
        try {
            enc_key = SHA256.getSHA(shared_secret[0].toString(16));
            String msg = Base64.encode(Aes.encryptText(new byte[] {(byte)command}, enc_key));

            byte[] msg_bytes = msg.getBytes(StandardCharsets.UTF_8);
            byte[] bytes = new byte[msg_bytes.length + 1];
            System.arraycopy(msg_bytes, 0, bytes, 1, msg_bytes.length);
            bytes[0] = (byte) command;

            s.getOutputStream().write(bytes, 0, bytes.length);
            s.getOutputStream().flush();
        } catch (Exception e) {
            Disconnect();
        }
    }

    public void Disconnect()
    {
        try {
            //writer.close();
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
                Write(msg, Commands.MESSAGE);
                System.out.println(msg);
            }
        }
    }
}