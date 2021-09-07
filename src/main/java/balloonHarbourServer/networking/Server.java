package balloonHarbourServer.networking;

import balloonHarbourServer.cryptography.ECC;
import balloonHarbourServer.users.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {

    private int port;
    private ECC ecc;
    private Thread t = null;
    public static List<User> onlineUsers = new ArrayList<User>();

    public Server(int port, ECC ecc) {
        this.port = port;
        this.ecc = ecc;
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
            System.out.println("[+] Server started on port " + port);

            while (t.isAlive()) {
                Socket client = socket.accept();
                User u = new User(client, ecc.genKeys());
                onlineUsers.add(u);

                System.out.println("[*] Client connected from (" + u.getIP() + ")");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void start() {
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
    }

    public void stop() {
        if (t != null) {
            t.stop();
        }
    }

    public static User getUserByName(String username) {
        for (User u : Server.onlineUsers) {
            if (u.username.equals(username)) {
                return u;
            }
        }
        return null;
    }
}