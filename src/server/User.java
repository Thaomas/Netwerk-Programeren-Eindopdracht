package server;

import client.gamelogic.Disc;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Runnable {

    private Socket socket;
    private DataInputStream in;
    private final Server server;
    private boolean isConnected;
    private final String name;
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
        this(name, password, server);
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.creationDate = creationDate;
    }

    @Override
    public void run() {
        isConnected = true;
        while (isConnected) {
            try {
                String received = this.in.readUTF();
                String command = received.substring(0, 4);
                switch (command) {
                    case "quit":
                        //Disconnect from the server
                        disconnect();
                        break;
                    case "Conn":
                        //Connect with the server
                        connect(received.substring(4, 8));
                        break;
                    case "Disc":
                        //Disconnect from the given Room
                        disconnectRoom(received.substring(4, 8));
                        break;
                    case "CMes":
                        //Chat message
                        chatMessage(received.substring(4, 8), received.substring(8));
                        break;
                    case "GMes":
                        //Game message
                        gameMessage(received.substring(4, 8), received.substring(8));
                        break;
                    case "GVot":
                        //Game vote
                        gameVote(received.substring(4, 8));
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
                        changePassword(received.substring(4));
                        break;
                    case "GLst":
                        //JoinGameGUI list of public games
                        sendGameRoomList(server.getGameRoomNames());
                        break;
                    case "GLea":
                        //Get leaderboard
                        new ObjectOutputStream(socket.getOutputStream()).writeObject(server.getLeaderboard());
                        break;
                    case "DelU":
                        //Deletes the user from the server
                        deleteUser(received.substring(4));
                        break;
                    default:
                        respond("Invalid command");
                        break;
                }
            } catch (IOException e) {
                e.getMessage();
                disconnect();
            }
        }
    }

    /**
     * Get the Json object to save.
     * @return Json Object
     */
    public JSONObject getJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", this.name);
        jsonObject.put("password", this.password);
        jsonObject.put("gamesPlayed", this.gamesPlayed);
        jsonObject.put("gamesWon", this.gamesWon);
        jsonObject.put("creationDate", this.creationDate.toString());
        return jsonObject;
    }

    /**
     * Send a message to a chat room.
     *
     * @param roomCode Code of the room the message is send to.
     * @param message  The message.
     */
    private void chatMessage(String roomCode, String message) {
        if (server.containsChatRoom(roomCode)) {
            server.writeToChatRoom(roomCode, this, message);
        } else if (server.containsGameRoom(roomCode)) {
            server.writeToGameRoom(roomCode, this, message);
        } else
            respond("Invalid room name");
    }

    /**
     * @param roomCode Code of the room the message is send to.
     * @param message  The mo
     */
    private void gameMessage(String roomCode, String message) {
        if (server.containsGameRoom(roomCode)) {
            GameRoom room = server.getGameRooms().get(roomCode);
            if (room.containsUser(this)) {
                respond("GMes" + roomCode);
                room.move(Integer.parseInt(message), this);
            }
        } else
            respond("Invalid room name");
    }

    private void gameVote(String roomCode) {
        if (server.containsGameRoom(roomCode)) {
            GameRoom room = server.getGameRooms().get(roomCode);
            if (room.containsUser(this)) {
                room.vote(this);
            }
        }
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

    /**
     * Send the move back to the user.
     *
     * @param disc Move to send the user.
     */
    public void sendDisc(Disc disc) {
        try {
            new ObjectOutputStream(socket.getOutputStream()).writeObject(disc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if given password is correct.
     *
     * @param in password to check.
     * @return is correct password.
     */
    public boolean checkPassword(String in) {
        return password.equals(in);
    }

    private void changePassword(String response) {
        if (response.substring(0, response.indexOf("|")).equals(password)) {
            response = response.substring(response.indexOf("|") + 1);
            int index = response.indexOf("|");
            if (response.substring(0, index).equals(response.substring(index + 1))) {
                password = response.substring(0, index);
                respond("Conf");
                server.save();
            }
        } else
            respond("IvPw");
    }

    /**
     * Delete the user.
     *
     * @param message Password to check.
     */
    private void deleteUser(String message) {
        if (checkPassword(password)) {
            respond("Account deleted");
            disconnect();
            server.deleteUser(this);
        } else {
            respond("Invalid password");
        }
    }

    /**
     * Add win to the user.
     */
    protected void win() {
        this.gamesPlayed++;
        this.gamesWon++;
    }

    /**
     * Add lose to the user.
     */
    protected void lose() {
        this.gamesPlayed++;
    }

    /**
     * Connect to chat or game room.
     *
     * @param roomCode The room code.
     */
    private void connect(String roomCode) {
        if (server.containsChatRoom(roomCode)) {
            server.connectToChatRoom(roomCode, this);
            respond("Conf");
            sendChatLog(server.getChatLog(roomCode));
        } else if (server.containsGameRoom(roomCode)) {
            if (server.connectToGameRoom(roomCode, this)) {
                respond("Conf" + server.getGameRooms().get(roomCode).getTurn(this));
                sendChatLog(server.getChatLog(roomCode));
                server.connectToChatRoom("main", this);
                sendChatLog(server.getChatLog("main"));
                server.getGameRooms().get(roomCode).hasJoined(this);
            } else
                respond("Full");
        } else
            respond("Invl");
    }

    /**
     * Disconnect from chat or game room.
     *
     * @param roomCode The room code.
     */
    private void disconnectRoom(String roomCode) {
        if (server.containsChatRoom(roomCode)) {
            server.disconnectChatRoom(roomCode, this);
            respond("Disc");
        } else if (server.containsGameRoom(roomCode)) {
            server.disconnectGameRoom(roomCode, this);
        } else
            respond("Invalid room name");
    }

    /**
     * Disconnect user from the server.
     */
    private void disconnect() {
        System.out.println("Disconnect user: " + name);
        isConnected = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Write a string back to the user.
     *
     * @param response Message to send back to the user.
     */
    public void respond(String response) {
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get total games played by user.
     *
     * @return Total games played.
     */
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Get total games won by user.
     *
     * @return Total games won.
     */
    public int getGamesWon() {
        return gamesWon;
    }

    /**
     * Set socket connected to user.
     *
     * @param socket Socket from connect user.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether user is connected.
     * @return is connected
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * Returns the name of the user.
     * @return Name of the user.
     */
    public String getName() {
        return name;
    }
}
