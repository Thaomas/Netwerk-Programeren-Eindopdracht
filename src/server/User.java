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
    private LocalDate creationDate;

    public User(String name, String password, Server server) {
        this.name = name;
        this.password = password;
        this.server = server;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.creationDate = LocalDate.now();
    }

    public User(String name, String password, int gamesPlayed, int gamesWon, LocalDate creationDate, Server server) {
        this(name,password,server);
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.creationDate = creationDate;
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

    public String getPassword() {
        return password;
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

    public boolean isConnected() {
        return isConnected;
    }

    @Override
    public void run() {
        System.out.println("Start user: " + name);
        isConnected = true;
        while (isConnected) {
            try {
                System.out.println("Start2 user: " + name);
                String received = this.in.readUTF();
                System.out.println(received);
                String command = received.substring(0, 4);

                String roomCode;
                switch (command) {
                    case "quit":
                        //Disconnect from the server
                        disconnect();
                        break;
                    case "Conn":
                        //Connect with the server
                        System.out.println("Connect " + name + " to " + received.substring(4, 8));
                        roomCode = received.substring(4, 8);
                        if (server.containsChatRoom(roomCode)) {
                            server.connectToChatRoom(roomCode, this);
                            System.out.println("Connected " + this.name + " to " + roomCode);
                            respond("Conf");
                            sendChatLog(server.getChatLog(roomCode));
                        } else if (server.containsGameRoom(roomCode)) {
                            if (server.connectToGameRoom(roomCode, this)) {
                                respond("Conf");
                                sendChatLog(server.getChatLog(roomCode));
                                server.connectToChatRoom("main", this);
                                sendChatLog(server.getChatLog("main"));
                                server.getGameRooms().get(roomCode).hasJoined();
                            } else
                                respond("Full");
                        } else
                            respond("Invalid");
                        break;
                    case "Disc":
                        //Disconnect from the given Room
                        System.out.println("test full received: " + received);
                        roomCode = received.substring(4, 8);
                        System.out.println("test roomcode: " + received);
                        if (server.containsChatRoom(roomCode)) {
                            server.disconnectChatRoom(roomCode, this);
                            respond("Disc");
                        } else if (server.containsGameRoom(roomCode)) {
                            server.disconnectGameRoom(roomCode, this);
                        } else
                            respond("Invalid room name");
                        break;
                    case "CMes":
                        //Chat message
                        roomCode = received.substring(4, 8);
                        if (server.containsChatRoom(roomCode)) {
                            server.writeToChatRoom(roomCode, this, received.substring(8));
                        } else if (server.containsGameRoom(roomCode)) {
                            server.writeToGameRoom(roomCode, this, received.substring(8));
                        } else
                            respond("Invalid room name");
                        break;
                    case "GMes":
                        //Game message
                        roomCode = received.substring(4, 8);
                        if (server.containsGameRoom(roomCode)) {
                            GameRoom room = server.getGameRooms().get(roomCode);
                            if (room.containsUser(this)) {
                                System.out.println("test roomcode GMes addDISC");
                                respond("GMes"+roomCode);
                                room.move(Integer.parseInt(received.substring(8)),this);
                            }
                        } else
                            respond("Invalid room name");
                        break;
                    case "GVot":
                        roomCode = received.substring(4, 8);
                        if (server.containsGameRoom(roomCode)) {
                            GameRoom room = server.getGameRooms().get(roomCode);
                            if (room.containsUser(this)) {
                                room.vote(this);
                            }
                        }
                        break;
                    case "CrCR":
                        //Create chat room
                        respond(server.newChatRoom(received.substring(4)));
                        break;
                    case "CrGR":
                        respond(server.newGameRoom(received.substring(4), this));
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
                        //JoinGameGUI list of public games
                        System.out.println("server: list call before" + received);
                        sendGameRoomList(server.getGameRoomNames());

                        System.out.println("server: list call after" + received);
                        break;
                    case "DelU":
                        //Deletes the user from the server
                        if(received.substring(4).equals(password)) {
                            respond("Account deleted");
                            disconnect();
                            server.deleteClient(this);
                        }else {
                            respond("Invalid password");
                        }
                        break;
                    default:
                        respond("Invalid command");
                        break;
                }
            } catch (IOException e) {
                System.out.println("error");
                e.getMessage();
                disconnect();
            }
        }
        System.out.println("Stopped running");
    }

    private void sendChatLog(ArrayList<String> chatLog) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(chatLog);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendGameRoomList(HashMap<String, String> gameRoomList) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(gameRoomList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDisc(Disc disc) {
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
        System.out.println("Disconnect user: " + name);
        isConnected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void respond(String response) {
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
