package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {
    private final int port = 10000;
    private ServerSocket serverSocket;

    private final HashMap<String, User> users;
    private final HashMap<String, User> connectedUsers;
    private final HashMap<String, Thread> clientThreads;
    private final HashMap<String, ChatRoom> chatRooms;
    private final HashMap<String, GameRoom> gameRooms;

    public static void main(String[] args) {

        System.out.println("Loading server");

        Server server = new Server();
        server.connect();
    }

    private void shutdown() {
        System.out.println("save");
        System.out.println();
        save();

        try {
            for (Thread thread : clientThreads.values()) {
                thread.join();
            }
            this.serverSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    Server() {
//        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        users = new HashMap<>();
        connectedUsers = new HashMap<>();
        clientThreads = new HashMap<>();
        chatRooms = new HashMap<>();
        gameRooms = new HashMap<>();
        load();
    }

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

    protected synchronized void save() {
        JSONObject object = new JSONObject();
        object.put("rooms", getRoomsArray());
        object.put("users", getUsersArray());

        try (FileWriter fileWriter = new FileWriter("saves/save.json")) {
            fileWriter.write(object.toJSONString());
            fileWriter.flush();
//            System.out.println("Save");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean containsGameRoom(String room) {
        return gameRooms.containsKey(room);
    }
    public boolean containsChatRoom(String room) {
        return chatRooms.containsKey(room);
    }

    protected void connectUser(User user) {
        connectedUsers.put(user.getName(), user);
    }

    public synchronized String newChatRoom(String roomName) {
        String roomcode = generateCode();
        addChatRoom(roomName, roomcode);
        return roomcode;
    }

    private String generateCode() {
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

    public String newGameRoom(String response, User user) {
        String roomcode = generateCode();
        boolean isPrivate = false;
        if (response.charAt(0) == 'p')
            isPrivate = true;
        addGameRoom(response.substring(1), roomcode, isPrivate);
        connectToGameRoom(roomcode,user);
        return roomcode;
    }

    private void addGameRoom(String roomName, String roomCode, boolean isPrivate) {
        System.out.println(chatRooms.keySet());
        if (!chatRooms.containsKey(roomCode)) {
            gameRooms.put(roomCode, new GameRoom(roomName, roomCode, isPrivate));
        }
    }


    public void connect() {
//        addChatRoom("Main", "main");
        Thread disconnectListener = new Thread(this::disconnectListener);
        disconnectListener.start();
        try {
            this.serverSocket = new ServerSocket(port);

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("Waiting for clients...");
                Socket socket = this.serverSocket.accept();
                System.out.println("Client connected via address: " + socket.getInetAddress().getHostAddress());
                System.out.println("Connected clients: " + this.connectedUsers.size());
                System.out.println("Total users: " + this.users.size());
                System.out.println("Total threads: " + this.clientThreads.size());
                Connector connection = new Connector(socket, this);
                Thread t = new Thread(connection);
                connection.setThread(t);
                t.start();
            }

//            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, GameRoom> getGameRooms() {
        return gameRooms;
    }

    public ArrayList<String> getChatLog(String serverName) {
        return chatRooms.get(serverName).getChatLog();
    }

    public synchronized boolean connectToGameRoom(String roomCode, User user) {
        return gameRooms.get(roomCode).addUser(user);
    }

    public void connectToChatRoom(String roomName, User user) {
        chatRooms.get(roomName).addUser(user);
    }

    public void disconnectChatRoom(String roomName, User user) {
        chatRooms.get(roomName).removeUser(user);
    }

    public void disconnectGameRoom(String roomName, User user) {
        gameRooms.get(roomName).removeUser(user);
    }

    public void writeToGameRoom(String roomName, User user, String message) {
        gameRooms.get(roomName).messageAll("<" + user.getName() + ">: " + message);
    }
    public void writeToChatRoom(String roomName, User user, String message) {
        chatRooms.get(roomName).messageAll("<" + user.getName() + ">: " + message);
    }

    private void disconnectListener() {
        int i = 0;
        while (true) {
            for (String key : clientThreads.keySet()) {
                if (!clientThreads.get(key).isAlive()) {
                    removeClient(users.get(key));
                    System.out.println(key + " is dead");
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

    public void removeClient(User user) {
        String nickname = user.getName();
        this.connectedUsers.remove(nickname);

        for (ChatRoom room : chatRooms.values()) {
            room.removeUser(user);
        }
        Thread t = this.clientThreads.get(nickname);
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.clientThreads.remove(nickname);

        System.out.println("Connected clients: " + this.users.size());
    }

    public HashMap<String, User> getUsers() {
        return users;
    }

    public synchronized void addUser(User user) {
        this.users.put(user.getName(), user);
        System.out.println("add user");
        save();
    }

    private void addChatRoom(String roomName, String roomCode) {
        System.out.println("Chatroom " + roomName + " has been added under code " +roomCode);
        if (!chatRooms.containsKey(roomCode)) {
            chatRooms.put(roomCode, new ChatRoom(roomName, roomCode));
            save();
        }
    }

    private JSONArray getRoomsArray() {
        JSONArray roomsArray = new JSONArray();
        for (ChatRoom chatRoom : chatRooms.values()) {
            roomsArray.add(chatRoom.getJSON());
        }
        return roomsArray;
    }

    private JSONArray getUsersArray() {
        JSONArray userArray = new JSONArray();
        for (User user : users.values()) {
            userArray.add(user.getJson());
        }
        return userArray;
    }

    public HashMap<String,String> getGameRoomNames(){
        HashMap<String, String> names = new HashMap<>();

        //TODO TEST DATA, DELETE AFTER TESTING
        HashMap<String,GameRoom> gameRooms = gameRoomHashMap();

        for (String code : gameRooms.keySet()) {
            if (!gameRooms.get(code).isPrivate() && gameRooms.get(code).getUsers().size() < 2) {
                names.put(code, gameRooms.get(code).getRoomName());
            }
        }

        return names;
    }

    public static HashMap<String,GameRoom> gameRoomHashMap(){
        HashMap<String,GameRoom> list = new HashMap<>();

        list.put("4251", new GameRoom("test 1","4251",true));
        list.put("5454", new GameRoom("Test not private","5454",false));
        list.put("0101", new GameRoom("test 2","0101",false));

        return list;
    }

    public void addClientThread(String name, Thread t) {
        this.clientThreads.put(name, t);
    }


}
