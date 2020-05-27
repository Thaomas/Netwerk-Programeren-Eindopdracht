package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class User implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private String password;
    private Server server;
    private boolean isConnected;
    private int gamesPlayed;
    private int gamesWon;
    private LocalDate creationDate;

    public User(String name, String password, Server server) {
        this.name = name;
        this.password = password;
        this.server = server;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.creationDate = LocalDate.now();
    }


    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public LocalDate getCreationDate() {
        return creationDate;
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
        System.out.println("Start " + name);
        isConnected = true;
        while (isConnected) {
            try {
                System.out.println("Start2 " + name);
                String received = this.in.readUTF();
                String command = received.substring(0, 4);
                System.out.println(received);
                if (received.equals("\\quit")) {
                    System.out.println("disconnect " + name);
                    disconnect();
                } else {
                    switch (command) {
                        case "Conn":
                            //Connect
                            System.out.println("Connect" + received.substring(4, 8));
                            if (server.containsRoom(received.substring(4, 8))) {
                                String serverName = received.substring(4, 8);
                                server.connectToChatRoom(serverName, this);
                                sendChatLog(server.getChatLog(serverName));
                                respond("Connected");
                            } else
                                respond("Invalid room name");
                            //todo make error-code and handling client side
                            break;
                        case "CMes":
                            //Chat message
                            System.out.println(received);
                            if (server.containsRoom(received.substring(4, 8))) {
                                server.writeToChatRoom(received.substring(4, 8), this, received.substring(8));
                            } else
                                respond("Invalid room name");
                            //todo make error-code and handling client side
                            break;
                        case "GMes":
                            //Game message

                            break;
                        case "Disc":
                            //Disconnect
                            if (server.containsRoom(received.substring(4, 8))) {
                                server.disconnectChatRoom(received.substring(4, 8), this);
                                respond("Disconnected");
                            } else
                                respond("Invalid room name");
                            //todo make error-code and handling client side
                            break;
                        case "CrCR":
                            //Create chat room
                            String code = server.newRoom(received.substring(4));
                            respond(code);
                            break;
                        case "CrGR":
                            //Create game room

                            break;
                        default:
                            respond("Invalid command");
                            //todo make error-code and handling client side
                            break;
                    }
                }
            } catch (SocketException e) {
                System.out.println("Disconnecting");
                disconnect();
            } catch (IOException e) {
                System.out.println("error");
                e.printStackTrace();
            }
        }
    }

    private void sendChatLog(ArrayList<String> chatlog) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(chatlog);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean checkPassword(String in) {
        return password.equals(in);
    }

    private void disconnect() {
        isConnected = false;
        this.server.removeClient(this);
    }

    private void respond(String response) {
        System.out.println(response);
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }
}
