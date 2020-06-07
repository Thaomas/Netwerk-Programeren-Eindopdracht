package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {
    private final int port = 10000;

    private final HashMap<String, User> users;
    private final HashMap<String, User> connectedUsers;
    private final HashMap<String, Thread> clientThreads;
    private final ArrayList<Thread> connectorThreads;
    private final HashMap<String, ChatRoom> chatRooms;
    private final HashMap<String, GameRoom> gameRooms;

    public static void main(String[] args) {
        Server server = new Server();
        server.connect();
    }

    Server() {
        users = new HashMap<>();
        connectedUsers = new HashMap<>();
        clientThreads = new HashMap<>();
        connectorThreads = new ArrayList<>();
        chatRooms = new HashMap<>();
        gameRooms = new HashMap<>();
        load();
    }

    /**
     * Saves all the server data to a JSON file.
     */
    protected synchronized void save() {
        JSONObject object = new JSONObject();
        object.put("rooms", getRoomsArray());
        object.put("users", getUsersArray());

        try (FileWriter fileWriter = new FileWriter("saves/save.json")) {
            fileWriter.write(object.toJSONString());
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all the saved data of the server when the server starts.
     */
    private void load() {
        JSONParser parser = new JSONParser();
        try (Reader reader = new FileReader("saves/save.json")) {
            JSONObject object = (JSONObject) parser.parse(reader);
            JSONArray roomArray = (JSONArray) object.get("rooms");
            JSONArray userArray = (JSONArray) object.get("users");

            for (Object o : roomArray) {
                JSONObject jsonObject = (JSONObject) o;
                String roomName = (String) jsonObject.get("roomName");
                String roomCode = (String) jsonObject.get("roomCode");
                ArrayList<String> chatlog = (ArrayList<String>) jsonObject.get("chatlog");
                chatRooms.put(roomCode, new ChatRoom(roomName, roomCode, chatlog));
            }

            for (Object o : userArray) {
                JSONObject jsonObject = (JSONObject) o;
                String name = (String) jsonObject.get("name");
                String password = (String) jsonObject.get("password");
                int gamesPlayed = (int) (long) jsonObject.get("gamesPlayed");
                int gamesWon = (int) (long) jsonObject.get("gamesWon");
                LocalDate creationDate = LocalDate.parse((String) jsonObject.get("creationDate"));
                users.put(name, new User(name, password, gamesPlayed, gamesWon, creationDate, this));
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the disconnect listener and connects Users to the server.
     */
    public void connect() {
        Thread disconnectListener = new Thread(this::disconnectListener);
        disconnectListener.start();
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (true) {
                System.out.println("Waiting for clients...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected via address: " + socket.getInetAddress().getHostAddress());
                System.out.println("Connected clients: " + this.connectedUsers.size());
                System.out.println("Total users: " + this.users.size());
                System.out.println("Total threads: " + this.getClientThreads().size());
                Connector connection = new Connector(socket, this);
                Thread t = new Thread(connection);
                t.start();
                getConnectorThreads().add(t);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the given GameRoom exists.
     *
     * @param room The code for the GameRoom.
     * @return If GameRoom exists
     */
    public boolean containsGameRoom(String room) {
        return gameRooms.containsKey(room);
    }

    /**
     * Check if the given ChatRoom exists.
     *
     * @param room The code for the ChatRoom.
     * @return If ChatRoom exists
     */
    public boolean containsChatRoom(String room) {
        return chatRooms.containsKey(room);
    }

    /**
     * Add an User to the connected Users.
     *
     * @param user The User to be added.
     */
    protected void connectUser(User user) {
        connectedUsers.put(user.getName(), user);
    }

    /**
     * Creates a new ChatRoom.
     *
     * @param roomName The name of the ChatRoom.
     * @return The code for the ChatRoom.
     */
    public synchronized String newChatRoom(String roomName) {
        String roomcode = generateCode();
        addChatRoom(roomName, roomcode);
        return roomcode;
    }

    /**
     * Generate a new 4 letter code that hasn't been used before.
     *
     * @return The 4 letter code.
     */
    private synchronized String generateCode() {
        Random random = new Random();
        StringBuilder roomcode = new StringBuilder();
        boolean validRoomCode = false;
        while (!validRoomCode) {
            roomcode = new StringBuilder();
            for (int i = 0; i <= 3; i++) {
                roomcode.append((char) (random.nextInt(26) + 'a'));
            }
            if (!chatRooms.containsKey(roomcode.toString()) && !gameRooms.containsKey(roomcode.toString())) {
                validRoomCode = true;
            }
        }
        return roomcode.toString();
    }

    /**
     * Creates a new GameRoom.
     *
     * @param response The message from the User.
     * @param user     The User that send the message.
     * @return The code for the new GameRoom
     */
    public String newGameRoom(String response, User user) {
        String roomcode = generateCode();
        boolean isPrivate = false;
        if (response.charAt(0) == 'p')
            isPrivate = true;
        gameRooms.put(roomcode, new GameRoom(response.substring(1), roomcode, isPrivate));
        connectToGameRoom(roomcode, user);
        return roomcode;
    }

    /**
     * Get a HashMap of all the GameRooms.
     *
     * @return A HashMap of the codes and the GameRooms.
     */
    public HashMap<String, GameRoom> getGameRooms() {
        return gameRooms;
    }

    /**
     * Get the chatlog of a room
     *
     * @param roomCode Code of the room
     * @return An arraylist of strings with all the chat messages from the room
     */
    public ArrayList<String> getChatLog(String roomCode) {
        ArrayList<String> chatlog = null;
        if (chatRooms.containsKey(roomCode)) {
            chatlog = chatRooms.get(roomCode).getChatLog();
        } else if (gameRooms.containsKey(roomCode)) {
            chatlog = gameRooms.get(roomCode).getChatLog();
        }
        return chatlog;
    }

    /**
     * Connect to a ChatRoom.
     *
     * @param roomCode Code of the ChatRoom.
     * @param user     User to connect.
     */
    public void connectToChatRoom(String roomCode, User user) {
        chatRooms.get(roomCode).addUser(user);
    }

    /**
     * Connect to a GameRoom.
     *
     * @param roomCode Code of the GameRoom.
     * @param user     User to connect.
     * @return If the user is added to the GameRoom.
     */
    public synchronized boolean connectToGameRoom(String roomCode, User user) {
        return gameRooms.get(roomCode).addUser(user);
    }

    /**
     * Disconnect from a ChatRoom.
     *
     * @param roomCode Code of the ChatRoom.
     * @param user     User to disconnect.
     */
    public void disconnectChatRoom(String roomCode, User user) {
        chatRooms.get(roomCode).removeUser(user);
    }

    /**
     * Disconnect from a GameRoom.
     *
     * @param roomCode Code of the GameRoom.
     * @param user     User to disconnect.
     */
    public void disconnectGameRoom(String roomCode, User user) {
        gameRooms.get(roomCode).removeUser(user);
    }

    /**
     * Write a message to a ChatRoom.
     *
     * @param roomCode Code of the ChatRoom.
     * @param user     User that send the message.
     * @param message  Message that the user send.
     */
    public void writeToChatRoom(String roomCode, User user, String message) {
        chatRooms.get(roomCode).messageAll("<" + user.getName() + ">: " + message);
    }

    /**
     * Write a message to a GameRoom.
     *
     * @param roomCode Code of the GameRoom.
     * @param user     User that send the message.
     * @param message  Message that the user send.
     */
    public void writeToGameRoom(String roomCode, User user, String message) {
        gameRooms.get(roomCode).messageAll("<" + user.getName() + ">: " + message);
    }

    /**
     * Get all client threads.
     *
     * @return A arraylist of clientThreads.
     */
    private synchronized HashMap<String, Thread> getClientThreads() {
        return clientThreads;
    }

    /**
     * Get all connector threads.
     *
     * @return A arraylist of connctorThreads.
     */
    private synchronized ArrayList<Thread> getConnectorThreads() {
        return connectorThreads;
    }

    /**
     * Listener for disconnected users or empty game rooms.
     */
    private void disconnectListener() {
        int i = 0;
        while (true) {
            HashMap<String, Thread> threads = getClientThreads();
            for (String key : threads.keySet()) {
                if (!threads.get(key).isAlive()) {
                    removeClient(users.get(key));
                    System.out.println(key + " is dead");
                }
            }

            for (GameRoom room :
                    gameRooms.values()) {
                if (room.isEmpty()) {
                    gameRooms.remove(room.getRoomCode());
                }
            }

            if (i >= 50) {
                save();
                i = 0;
            } else
                i++;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Remove user from connected clients.
     *
     * @param user User to be disconnected.
     */
    private void removeClient(User user) {
        String nickname = user.getName();
        this.connectedUsers.remove(nickname);

        for (ChatRoom room : chatRooms.values()) {
            room.removeUser(user);
        }
        for (GameRoom gameRoom : gameRooms.values()) {
            gameRoom.removeUser(user);
        }
        Thread t = getClientThreads().get(nickname);
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getClientThreads().remove(nickname);

        System.out.println("Connected clients: " + this.connectedUsers.size());
    }

    /**
     * Delete a User from the server.
     *
     * @param user User to be removed.
     */
    public synchronized void deleteUser(User user) {
        users.remove(user.getName());
        save();
    }

    /**
     * Get the user list.
     *
     * @return A HashMap of names of users and users.
     */
    public HashMap<String, User> getUsers() {
        return users;
    }

    /**
     * Add user to user list.
     *
     * @param user User to be added.
     */
    public synchronized void addUser(User user) {
        this.users.put(user.getName(), user);
        save();
    }

    /**
     * Create a new chat room.
     *
     * @param roomName Chat room name.
     * @param roomCode Chat room code.
     */
    private void addChatRoom(String roomName, String roomCode) {
        if (!chatRooms.containsKey(roomCode)) {
            chatRooms.put(roomCode, new ChatRoom(roomName, roomCode));
            save();
        }
    }

    /**
     * Get JSONArray of roomdata.
     *
     * @return JSONArray of roomdata.
     */
    private JSONArray getRoomsArray() {
        JSONArray roomsArray = new JSONArray();
        for (ChatRoom chatRoom : chatRooms.values()) {
            roomsArray.add(chatRoom.getJSON());
        }
        return roomsArray;
    }

    /**
     * Get JSONArray of userdata.
     *
     * @return JSONArray of userdata.
     */
    private JSONArray getUsersArray() {
        JSONArray userArray = new JSONArray();
        for (User user : users.values()) {
            userArray.add(user.getJson());
        }
        return userArray;
    }

    /**
     * Add thread to the client thread list
     *
     * @param name name of the user.
     * @param t    Thread the user is running on.
     */
    public void addClientThread(String name, Thread t) {
        getClientThreads().put(name, t);
    }

    /**
     * Get all the room names and room codes
     *
     * @return A HashMap of codes and names
     */
    public HashMap<String, String> getGameRoomNames() {
        HashMap<String, String> names = new HashMap<>();
        for (GameRoom room : this.getGameRooms().values()) {
            if (!room.isPrivate() && room.checkSpace()) {
                names.put(room.getRoomCode(), room.getRoomName());
            }
        }
        return names;
    }

    /**
     * Get the stats from all the users.
     *
     * @return A HashMap of all the users and the times they played and won.
     */
    public HashMap<String, HashMap<String, Integer>> getLeaderboard() {
        HashMap<String, HashMap<String, Integer>> leaderboard = new HashMap<>();
        HashMap<String, Integer> values;
        for (User user : users.values()) {
            values = new HashMap<>();
            values.put("played", user.getGamesPlayed());
            values.put("won", user.getGamesWon());
            leaderboard.put(user.getName(), values);
        }
        return leaderboard;
    }
}
