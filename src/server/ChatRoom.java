package server;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom implements Room {
    private final HashSet<User> users;
    private final String roomName;
    private String roomCode;
    private final ArrayList<String> chatLog;

    public ChatRoom(String roomname) {
        users = new HashSet<>();
        this.roomName = roomname;
        chatLog = new ArrayList<>();
    }

    public void setRoomCode(String code){
        roomCode = code;
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
            user.writeUTF("CMes"+message);
        }
    }

}
