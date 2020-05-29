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
    private final HashMap<String, ChatRoom> rooms;

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
        rooms = new HashMap<>();
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
                rooms.put(roomCode, new ChatRoom(roomName, roomCode, chatlog));
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

    private synchronized void save() {
        JSONObject object = new JSONObject();
        object.put("rooms", getRoomsArray());
        object.put("users", getUsersArray());

        try (FileWriter fileWriter = new FileWriter("saves/save.json")) {
            fileWriter.write(object.toJSONString());
            fileWriter.flush();
            System.out.println("Save");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsRoom(String room) {
        return rooms.containsKey(room);
    }

    protected void connectUser(User user) {
        connectedUsers.put(user.getName(), user);
    }

    public synchronized String newRoom(String roomname) {
        Random random = new Random();
        StringBuilder roomcode = new StringBuilder();
        boolean validRoomCode = false;
        while (!validRoomCode) {
            roomcode = new StringBuilder();
            for (int i = 0; i <= 3; i++) {
                roomcode.append((char) (random.nextInt(26) + 'a'));
            }
            if (!rooms.containsKey(roomcode.toString())) {
                validRoomCode = true;
            }
        }
        addChatRoom(roomname, roomcode.toString());
        return roomcode.toString();
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

    public ArrayList<String> getChatLog(String serverName){
        return rooms.get(serverName).getChatLog();
    }

    public void connectToChatRoom(String roomName, User user) {
        rooms.get(roomName).addUser(user);
    }

    public void disconnectChatRoom(String roomName, User user) {
        rooms.get(roomName).removeUser(user);
    }

    public void writeToChatRoom(String roomName, User user, String message) {
        ChatRoom room = rooms.get(roomName);
        room.messageAll("<" + user.getName() + ">: " + message);
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

        for (ChatRoom room : rooms.values()) {
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
        System.out.println(rooms.keySet());
        if (!rooms.containsKey(roomCode)) {
            rooms.put(roomCode, new ChatRoom(roomName, roomCode));
            save();
        }
    }

    private JSONArray getRoomsArray() {
        JSONArray roomsArray = new JSONArray();
        for (ChatRoom chatRoom : rooms.values()) {
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

    public void addClientThread(String name, Thread t) {
        this.clientThreads.put(name, t);
    }
}
