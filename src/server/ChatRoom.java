package server;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom implements Room {
    private final HashSet<User> users;
    private final ArrayList<String> chatLog;

    public ChatRoom() {
        users = new HashSet<>();
        chatLog = new ArrayList<>();
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
            user.writeUTF(message);
        }
    }

}
