package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.ArrayList;

public class GameRoom {
    private final boolean isPrivate;
    private boolean inProgress;
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private User red;
    private User yellow;
    private final ConnectFour connectFour;

    public GameRoom(String roomname, String roomcode, boolean isPrivate) {
        System.out.println(roomname + " " + isPrivate);
        this.roomName = roomname;
        this.roomCode = roomcode;
        this.isPrivate = isPrivate;
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
        if (red == null || yellow == null) {
            if (red == null) {
                red = user;
            } else {
                yellow = user;
            }
            return true;
        } else
            return false;
    }

    public boolean containsUser(User user) {
        return red.equals(user) || yellow.equals(user);
    }

    public void removeUser(User user) {
        if (red == user || yellow == user) {
            if (red == user) {
                if (inProgress) {
                    win(yellow);
                    lose(red);
                }
                red = null;
            } else {
                if (inProgress) {
                    win(red);
                    lose(yellow);
                }
                yellow = null;
            }
        }
    }

    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    public synchronized boolean checkSpace() {
        return red == null || yellow == null;
    }

    public void move(int column, User user) {
        if (red != null && yellow != null) {
            if (user.equals(red)) {
                moveAll(connectFour.placeDisc(column, Color.red));
                if (connectFour.checkWin(Color.red)) {
                    win(red);
                    lose(yellow);
                }
            } else if (user.equals(yellow)) {
                moveAll(connectFour.placeDisc(column, Color.yellow));
                if (connectFour.checkWin(Color.yellow)) {
                    win(yellow);
                    lose(red);
                }
            }
        }
    }

    private void win(User winner) {
        winner.win();
        winner.respond("GMes" + roomCode + "Win");
    }

    private void lose(User loser) {
        loser.lose();
        loser.respond("GMes" + roomCode + "Lose");
    }

    //TODO
    public synchronized void hasJoined(){
        if (red != null && yellow != null)
        red.respond("GMes" + roomCode + "Conn" + yellow.getName());
        if (yellow != null)
        yellow.respond("GMes" + roomCode + "Conn" + red.getName());
    }

    private synchronized void moveAll(Disc disc) {
        red.respond("GMes" + roomCode + "Move");
        red.sendDisc(disc);
        yellow.respond("GMes" + roomCode + "Move");
        yellow.sendDisc(disc);
    }

    public synchronized void messageAll(String message) {
        chatlog.add(message);
        if (red != null)
            red.respond("CMes" + roomCode + message);
        if (yellow != null)
            yellow.respond("CMes" + roomCode + message);
    }
}
