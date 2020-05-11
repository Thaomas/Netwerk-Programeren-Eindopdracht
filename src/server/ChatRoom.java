package server;

import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom implements Room {
    private HashSet<User> users;
    private ArrayList<String> chatlog;

    public ChatRoom() {
        users = new HashSet();
        chatlog = new ArrayList<>();
    }

    public void addUser(User user){
        users.add(user);
    }

    public ArrayList<String> getChatlog() {
        return chatlog;
    }
    public void messageAll(String message){
        chatlog.add(message);
        for (User user:users) {
            user.writeUTF(message);
        }
    }
}
