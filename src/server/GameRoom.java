package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class GameRoom {
    private final boolean isPrivate;
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private final HashMap<User, Color> users;
    private final ConnectFour connectFour;

    public GameRoom(String roomname, String roomcode, boolean isPrivate) {
        System.out.println(roomname + " " + isPrivate);
        this.roomName = roomname;
        this.roomCode = roomcode;
        this.isPrivate = isPrivate;
        users = new HashMap<>();
        chatlog = new ArrayList<>();
        connectFour = new ConnectFour();
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
            if (users.containsValue(Color.red)) {
                users.put(user, Color.yellow);
            } else {
                users.put(user, Color.red);
            }
            return true;
        } else
            return false;
    }

    public boolean containsUser(User user) {
        return users.containsKey(user);
    }

    public void removeUser(User user) {
        if (users.containsKey(user)) {
            users.remove(user);
            lose(user);
            for (User winner : users.keySet())
                win(winner);
        }
    }

    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    public HashMap<User, Color> getUsers() {
        return users;
    }

    public Disc move(int column, User user) {
        if (connectFour.checkGameState(users.get(user))) {
            return null;
        } else {
            return connectFour.placeDisc(column, users.get(user));
        }
    }

    private void win(User winner) {
        winner.win();
    }

    private void lose(User loser) {
        loser.lose();
    }

    public synchronized void messageAll(String message) {
        chatlog.add(message);
        for (User user : users.keySet()) {
            user.writeUTF("CMes" + roomCode + message);
        }
    }
}
