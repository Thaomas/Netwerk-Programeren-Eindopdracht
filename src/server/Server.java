package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Server {

    private final int port = 10000;
    private ServerSocket serverSocket;

    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, User> connectedUsers = new HashMap<>();
    //private ArrayList<Thread> clientThreads = new ArrayList<>();
    private HashMap<String, Thread> clientThreads = new HashMap<>();
    private HashMap<String, ChatRoom> rooms = new HashMap<>();

    public static void main(String[] args) {

        System.out.println("Loading server");
        Server server = new Server();
        server.connect();
    }

    public boolean containsRoom(String room) {
        return rooms.containsKey(room);
    }

    protected void connectUser(User user) {
        connectedUsers.put(user.getName(), user);
    }


    public void connect() {

        rooms.put("Main", new ChatRoom());

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
            this.serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> getChatLog(String servername){
        return rooms.get(servername).getChatlog();
    }

    public void connectToChatRoom(String roomName, User user) {
        rooms.get(roomName).addUser(user);
    }

    public void disconnectChatRoom(String roomName, User user) {
        rooms.get(roomName).removeUser(user);
    }

    public void writeToChatRoom(String roomName, User user, String message) {
        ChatRoom room = rooms.get(roomName);
        if (!room.checkUser(user)) {
            room.addUser(user);
        }
        room.messageAll("<" + user + ">: " + message);
    }

    public void removeClient(User user) {
        String nickname = user.getName();
        this.connectedUsers.remove(user.getName());


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

    public void addClientThread(String name, Thread t) {
        this.clientThreads.put(name, t);
    }
}
