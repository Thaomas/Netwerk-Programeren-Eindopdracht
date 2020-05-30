package server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Server {

    private final int port = 10000;
    private ServerSocket serverSocket;

    private HashMap<String, User> users;
    private HashMap<String, User> connectedUsers;
    private HashMap<String, Thread> clientThreads;
    private HashMap<String, ChatRoom> rooms;

    public static void main(String[] args) {

        System.out.println("Loading server");

        Server server = new Server();
        server.connect();
    }

    private void shutdown() {
        System.out.println("save");
        System.out.println();
        JSONArray array = new JSONArray();
        System.out.println(users);
        array.put(new JSONObject(users));
        array.put(new JSONObject(rooms));
        try (FileWriter file = new FileWriter("saves/save.json")) {

            file.write(array.toString());
            file.flush();
            System.out.println("Save");
        } catch (IOException e) {
            e.printStackTrace();
        }

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
    }

    public boolean containsRoom(String room) {
        return rooms.containsKey(room);
    }

    protected void connectUser(User user) {
        connectedUsers.put(user.getName(), user);
    }

    public synchronized String newRoom(String roomname) {
        Random random = new Random();
        String roomcode = "";
        boolean validRoomCode = false;
        while (!validRoomCode) {
            roomcode = "";
            for (int i = 0; i <= 3; i++) {
                roomcode += (char) (random.nextInt(26) + 'a');
            }
            if (!rooms.containsKey(roomcode)) {
                validRoomCode = true;
            }
        }
        addChatRoom(roomcode, new ChatRoom(roomname));
        return roomcode;
    }


    public void connect() {
        addChatRoom("main", new ChatRoom("Main"));

        try {
            this.serverSocket = new ServerSocket(port);

            boolean isRunning = true;
            while (isRunning) {
                System.out.println("Waiting for clients...");
                Socket socket = this.serverSocket.accept();
                System.out.println("Client connected via address: " + socket.getInetAddress().getHostAddress());
                System.out.println("Connected clients: " + this.connectedUsers.size());
                System.out.println("Total users: " + this.users.size());
                Connecter connection = new Connecter(socket, this);
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
        room.messageAll("<" + user + ">: " + message);
    }

    public void removeClient(User user) {
        String nickname = user.getName();
        this.connectedUsers.remove(user.getName());
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
//        saveUsers();
    }

    private void addChatRoom(String roomcode, ChatRoom room) {
        rooms.put(roomcode, room);
//        saveRoom();
    }

    public void writeToLogic(){

    }

//    private synchronized void saveRoom() {
//        File file = new File("saves/save.json");
//        JSONObject object;
//        if (file.exists())
//            object = new JSONObject(file);
//        else
//            object = new JSONObject();
//
//        object.put("rooms", new JSONObject(rooms));
//        try (FileWriter fileWriter = new FileWriter("saves/save.json")) {
//            fileWriter.write(object.toString());
//            fileWriter.flush();
//            System.out.println("Save");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private synchronized void saveUsers() {
//        File file = new File("saves/save.json");
//        JSONObject object;
//        if (file.exists())
//            object = new JSONObject(file);
//        else
//            object = new JSONObject();
//        object.put("users", new JSONObject(users));
//        try (FileWriter fileWriter = new FileWriter("saves/save.json")) {
//            fileWriter.write(object.toString());
//            System.out.println(object.toString());
//            fileWriter.flush();
//            System.out.println("Save");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


    public void addClientThread(String name, Thread t) {
        this.clientThreads.put(name, t);
    }
}
