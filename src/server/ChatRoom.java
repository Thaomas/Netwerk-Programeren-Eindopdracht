package server;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom implements Room {
    private final HashSet<User> users;
    private final String roomname;
    private String roomcode;
    private final ArrayList<String> chatLog;

    public ChatRoom(String roomname) {
        users = new HashSet<>();
        this.roomname = roomname;
        chatLog = new ArrayList<>();
    }

    public void setRoomcode(String code){
        roomcode = code;
    }

    public String getRoomcode() {
        return roomcode;
    }

    public String getRoomname() {
        return roomname;
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
