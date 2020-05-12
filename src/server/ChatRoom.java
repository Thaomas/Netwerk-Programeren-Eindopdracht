package server;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom implements Room {
    private final HashSet<User> users;
    private final ArrayList<String> chatlog;

    public ChatRoom() {
        users = new HashSet<>();
        chatlog = new ArrayList<>();
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

    public ArrayList<String> getChatlog() {
        return chatlog;
    }

    public void messageAll(String message) {
        chatlog.add(message);
        for (User user : users) {
            user.writeUTF(message);
        }
    }

}
