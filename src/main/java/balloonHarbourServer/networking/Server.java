package balloonHarbourServer.networking;

import balloonHarbourServer.users.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.stream.Stream;

public class Server implements Runnable {

    private int port;
    private Thread t = null;
    public List<User> onlineUsers;

    public Server(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket socket = new ServerSocket(port);
            System.out.println("[+] Server started on port " + port);

            while (t.isAlive()) {
                Socket client = socket.accept();
                System.out.println("[*] Client connected from (" + socket.getInetAddress() + ")");

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));

                onlineUsers.add(new User(reader, writer, client));
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
}