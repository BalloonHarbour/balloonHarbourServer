package balloonHarbourServer.users;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class User implements Runnable {

    private String username, color;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket s;
    private Thread t;

    public User(BufferedReader reader, BufferedWriter writer, Socket s) {
        this.reader = reader;
        this.writer = writer;
        this.s = s;

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try {
            StringBuilder sb = new StringBuilder();

            while (t.isAlive()) {
                while (reader.ready()) {
                    sb.append((char)reader.read());
                }

                InterpretMessage(sb.toString());
                sb.setLength(0);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void InterpretMessage(String msg) {
        if (msg.length() > 1) {
            switch (msg.substring(0, 2)) {
                case "li":
                    break;

                case "lo":
                    break;

                case "dc":
                    break;
            }
        }
    }

    public void Disconnect() {
        if (t != null) {
            try {
                reader.close();
                writer.close();
                s.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            t.stop();
        }
    }
}