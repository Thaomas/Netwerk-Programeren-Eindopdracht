package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom {
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private final HashSet<User> users;

    public ChatRoom(String roomname, String roomcode) {
        this.roomName = roomname;
        this.roomCode = roomcode;
        testChat(100);
        users = new HashSet<>();
        chatlog = new ArrayList<>();
    }

    public ChatRoom(String roomName, String roomCode, ArrayList<String> chatlog) {
        this.roomName = roomName;
        this.roomCode = roomCode;
        this.chatlog = chatlog;
        this.users = new HashSet<>();
    }

    private void testChat(int amount) {
        System.out.println(this.roomName);
        System.out.println(this.roomCode);
        System.out.println(this.chatlog);
        for (int i = 0; i < amount; i++) {
            chatlog.add("test " + i+"\n");
        }
    }

    public JSONObject getJSON(){
        JSONObject object = new JSONObject();
        object.put("roomName", this.roomName);
        object.put("roomCode", this.roomCode);
        object.put("chatlog",  this.chatlog);
        return object;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public String getRoomName() {
        return roomName;
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(User user) {
        users.remove(user);
    }

    public boolean checkUser(User user) {
        return users.contains(user);
    }

    public ArrayList<String> getChatLog() {
        return chatLog;
    }

    public void messageAll(String message) {
        chatLog.add(message);
        for (User user : users) {
            user.writeUTF("CMes" + message);
        }
    }

}
