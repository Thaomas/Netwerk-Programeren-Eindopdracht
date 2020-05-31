package server;

import java.util.ArrayList;
import java.util.HashSet;

public class GameRoom {
    private final boolean isPrivate;
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private final HashSet<User> users;

    public GameRoom(String roomname, String roomcode, boolean isPrivate) {
        System.out.println(roomname + " " + isPrivate);
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

    public synchronized boolean addUser(User user) {
        if (users.size() < 2) {
            users.add(user);
            return true;
        } else
            return false;
    }

    public boolean containsUser(User user) {
        return users.contains(user);
    }

    public void removeUser(User user) {
        if (users.contains(user)) {
            users.remove(user);
            lose(user);
            for (User winner : users)
                win(winner);
        }
    }

    public boolean checkUser(User user) {
        return users.contains(user);
    }

    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    public HashSet<User> getUsers() {
        return users;
    }

    public void move(String move) {

    }

    private void win(User winner) {
        winner.win();
    }

    private void lose(User loser) {
        loser.lose();
    }

    public void messageAll(String message) {
        chatlog.add(message);
        for (User user : users) {
            user.writeUTF("CMes" + message);
        }
    }
}
