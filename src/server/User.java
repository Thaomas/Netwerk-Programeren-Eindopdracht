package server;

import client.gamelogic.Disc;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

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
    private Color color;

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

    public void setColor(Color color) {
        this.color = color;
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
                //Connect
                String roomCode;
                switch (command) {
                    case "quit":
                        disconnect();
                        break;
                    case "Conn":
                        System.out.println("Connect " + name + " to " + received.substring(4, 8));
                        roomCode = received.substring(4, 8);
                        if (server.containsChatRoom(roomCode)) {
                            server.connectToChatRoom(roomCode, this);
                            respond("Connected");
                            sendChatLog(server.getChatLog(roomCode));
                        } else if (server.containsGameRoom(roomCode)) {
                            if (server.connectToGameRoom(roomCode, this))
                                respond("Conf");
                            else
                                respond("Full");
                        } else
                            respond("Invalid room name");
                        //todo make error-code and handeling client side
                        break;
                    case "Disc":
                        roomCode = received.substring(4, 8);
                        if (server.containsChatRoom(roomCode)) {
                            server.disconnectChatRoom(roomCode, this);
                            respond("Disc");
                        } else if (server.containsGameRoom(roomCode)) {
                            server.disconnectGameRoom(roomCode, this);
                        } else
                            respond("Invalid room name");
                        //todo make error-code and handeling client side
                        break;
                    case "CMes":
                        roomCode = received.substring(4, 8);
                        if (server.containsChatRoom(roomCode)) {
                            server.writeToChatRoom(roomCode, this, received.substring(8));
                        } else if (server.containsGameRoom(roomCode)) {
                            server.writeToGameRoom(roomCode, this, received.substring(8));
                        } else
                            respond("Invalid room name");
                        //todo make error-code and handeling client side
                        break;
                    case "GMes":
                        //Game message
                        roomCode = received.substring(4, 8);
                        if (server.containsGameRoom(roomCode)) {
                            GameRoom room = server.getGameRooms().get(roomCode);
                            if (room.containsUser(this)) {
                                System.out.println("test roomcode GMes addDISC");
                                addDisc(room.move(Integer.parseInt(received.substring(8))));
                            }
                        } else
                            respond("Invalid room name");
                        break;
                    case "CrCR":
                        //Create chat room
                        respond(server.newChatRoom(received.substring(4)));
                        break;
                    case "CrGR":
                        respond(server.newGameRoom(received.substring(4),this));
                        //Create game room
                        break;
                    case "GUsD":
                        //Get User Data
                        respond(name + "/" + gamesPlayed + "/" + gamesWon + "/" + creationDate.toString());
                        break;
                    case "ChPw":
                        //Change User Password
                        if (changePassword(received.substring(4))) {
                            respond("Conf");
                            server.save();
                        } else
                            respond("IvPw");
                        break;
                    case "GLst":
                        //TODO
                        sendGameRoomList(server.getGameRoomNames());
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
                disconnect();
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

    private void sendGameRoomList(HashMap<String, String> gameRoomList){
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(gameRoomList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addDisc(Disc disc){
        try {
            System.out.println("in disc");
            new ObjectOutputStream(socket.getOutputStream()).writeObject(disc);
            System.out.println("out disc");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkPassword(String in) {
        return password.equals(in);
    }

    private boolean changePassword(String response) {
        if (response.substring(0, response.indexOf("|")).equals(password)) {
            response = response.substring(response.indexOf("|") + 1);
            int index = response.indexOf("|");
            if (response.substring(0, index).equals(response.substring(index + 1))) {
                password = response.substring(0, index);
                return true;
            }
        }
        return false;
    }

    protected void lose() {
        this.gamesPlayed++;
    }

    protected void win() {
        this.gamesPlayed++;
        this.gamesWon++;
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
