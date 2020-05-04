package server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

public class ServerClient implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private Server server;
    private boolean isConnected = true;

    public ServerClient(Socket socket, String name, Server server) {
        this.socket = socket;
        this.name = name;
        this.server = server;

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

    @Override
    public void run() {
        while (isConnected) {
            try {
                String received = this.in.readUTF();
                if (received.equals("\\quit")) {
                    isConnected = false;
                    this.server.removeClient(this);
                } else {
                    this.server.sendToAllClients("<" + this.name + "> : " + received);
                }
            } catch (SocketException e){
             isConnected = false;
             this.server.removeClient(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }
}
