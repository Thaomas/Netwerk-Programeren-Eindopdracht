package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class User implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private String password;
    private Server server;
    private boolean isConnected;

    public User(String name, String password, Server server) {
        this.name = name;
        this.password = password;
        this.server = server;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeUTF(String text) {
        try {
            this.out.writeUTF(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void run() {
        isConnected = true;
        while (isConnected) {
            try {
                String received = this.in.readUTF();
                if (received.equals("\\quit")) {
                    System.out.println("disconnect");
                    isConnected = false;
                    this.server.removeClient(this);
                } else if (server.containsRoom(received.substring(0,3)));
            } catch (SocketException e){
             isConnected = false;
             this.server.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkPassword(String in){
        return password.equals(in);
    }

    public String getName() {
        return name;
    }
}
