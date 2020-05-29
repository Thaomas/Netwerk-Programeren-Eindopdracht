package server;

import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class User implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private boolean isConnected;
    private String name;
    private String password;
    private int gamesPlayed;
    private int gamesWon;
    private final LocalDate creationDate;

    public User(String name, String password, Server server) {
        this.name = name;
        this.password = password;
        this.server = server;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.creationDate = LocalDate.now();
    }

    public User(String name, String password, int gamesPlayed, int gamesWon, LocalDate creationDate, Server server) {
        this.name = name;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.creationDate = creationDate;
        this.server = server;
    }

    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.name);
        jsonObject.put("password", this.password);
        jsonObject.put("gamesPlayed", this.gamesPlayed);
        jsonObject.put("gamesWon", this.gamesWon);
        jsonObject.put("creationDate", this.creationDate.toString());
        return jsonObject;
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
                System.out.println(received);
                String command = received.substring(0, 4);
                switch (command) {
                    case "quit":
                        disconnect();
                        break;
                    case "Conn":
                        System.out.println("Connect " + name + " to " + received.substring(4, 8));
                        if (server.containsRoom(received.substring(4, 8))) {
                            String servername = received.substring(4, 8);
                            server.connectToChatRoom(servername, this);
                            respond("Connected");
                            sendChatLog(server.getChatLog(servername));
                        } else
                            respond("Invalid room name");
                        //todo make error-code and handeling client side
                        break;
                    case "Disc":
                        if (server.containsRoom(received.substring(4, 8))) {
                            server.disconnectChatRoom(received.substring(4, 8), this);
                            respond("Disc");
                        } else
                            respond("Invalid room name");
                        //todo make error-code and handeling client side
                        break;
                    case "CMes":
                        System.out.println(received);
                        if (server.containsRoom(received.substring(4, 8))) {
                            server.writeToChatRoom(received.substring(4, 8), this, received.substring(8));
                        } else
                            respond("Invalid room name");
                        //todo make error-code and handeling client side
                        break;
                    case "GMes":

                        break;
                    case "CrCR":
                        String code = server.newRoom(received.substring(4));
                        respond(code);
                        break;
                    case "CrGR":

                        break;
                    default:
                        respond("Invalid command");
                        //todo make error-code and handeling client side
                        break;
                }
            } catch (SocketException e) {
//                e.printStackTrace();
                disconnect();
            } catch (IOException e) {
                System.out.println("error");
                e.printStackTrace();
            }
        }
        System.out.println("Stopped running");
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
        System.out.println("Disconnect " + name);
        isConnected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
