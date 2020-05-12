package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

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

    public void setPassword(String password) {
        this.password = password;
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
                    System.out.println("disconnect "+ name);
                    disconnect();
                } else {
                    switch (received.substring(0, 4)) {
                        case "Conn":
                            if (server.containsRoom(received.substring(4, 8))) {
                                String servername = received.substring(4, 8);
                                server.connectToChatRoom(servername, this);
                                sendChatLog(server.getChatLog(servername));
                                respond("Connected");
                            } else
                                respond("Invalid room name");
                            //todo make error-code and handeling client side
                            break;
                        case "Send":
                            if (server.containsRoom(received.substring(4, 8))) {
                                server.writeToChatRoom(received.substring(4, 8), this, received.substring(4));
                                respond("Connected");
                            }
                            else
                                respond("Invalid room name");
                            //todo make error-code and handeling client side
                            break;
                        case "Disc":
                            if (server.containsRoom(received.substring(4, 8))) {
                                server.disconnectChatRoom(received.substring(4, 8), this);
                                respond("Connected");
                            }
                            else
                                respond("Invalid room name");
                            //todo make error-code and handeling client side
                            break;
                        default:
                            respond("Invalid command");
                            //todo make error-code and handeling client side
                            break;
                    }
                }
            } catch (SocketException e) {
                disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendChatLog(ArrayList<String> chatlog){
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(chatlog);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean checkPassword(String in) {
        return password.equals(in);
    }

    private void disconnect(){
        isConnected = false;
        this.server.removeClient(this);
    }

    private void respond(String response) {
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(response);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
