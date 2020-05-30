package server;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

public class GameRoom {
    private final boolean isPrivate;
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private final HashSet<User> users;

    public GameRoom(String roomname, String roomcode, boolean isPrivate) {
        this.roomName = roomname;
        this.roomCode = roomcode;
        this.isPrivate = isPrivate;
        users = new HashSet<>();
        chatlog = new ArrayList<>();
//        testChat(100);
    }

    private void testChat(int amount) {
        System.out.println(this.roomName);
        System.out.println(this.roomCode);
        System.out.println(this.chatlog);
        for (int i = 0; i < amount; i++) {
            chatlog.add("test " + i + "\n");
        }
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public boolean addUser(User user) {
        if (users.size() < 2) {
            users.add(user);
            return true;
        } else
            return false;
    }

    public boolean containsUser(User user){
        return users.contains(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public boolean checkUser(User user) {
        return users.contains(user);
    }

    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    public void move(String move) {

    }

    public void messageAll(String message) {
        chatlog.add(message);
        for (User user : users) {
            user.writeUTF("CMes" + message);
        }
    }
}
