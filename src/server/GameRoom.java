package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.ArrayList;

public class GameRoom {
    private final boolean isPrivate;
    private boolean inProgress;
    private boolean isFinished;
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private User red;
    private User yellow;
    private final ConnectFour connectFour;
    private boolean voteRed = false;
    private boolean voteYellow = false;


    public GameRoom(String roomname, String roomcode, boolean isPrivate) {
        System.out.println(roomname + " " + isPrivate);
        this.roomName = roomname;
        this.roomCode = roomcode;
        this.isPrivate = isPrivate;
        this.isFinished = false;
        chatlog = new ArrayList<>();
        connectFour = new ConnectFour();
    }
    
    public boolean isEmpty() {
        return red == null && yellow == null;
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
                    System.out.println(user.getName() + "left");
                    yellow.win();
                    red.lose();
                    inProgress = false;
                    connectFour.restart();
                    yellow.respond("GMes" + roomCode + "Restart");
                }
                red = null;
            } else {
                if (inProgress) {
                    System.out.println(user.getName() + "left");
                    red.win();
                    yellow.lose();
                    inProgress = false;
                    connectFour.restart();
                    red.respond("GMes" + roomCode + "Restart");
                }
                yellow = null;
            }
        }
        if (red != null) {
            red.respond("CMes" + roomCode + user.getName() + " has disconnected!");
        }
        if (yellow != null) {
            yellow.respond("CMes" + roomCode + user.getName() + " has disconnected!");
        }
    }

    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    public synchronized boolean checkSpace() {
        return red == null || yellow == null;
    }

    public synchronized void move(int column, User user) {
        if (red != null && yellow != null && !isFinished) {
            if (!inProgress)
                inProgress = true;

            if (user.equals(red)) {
                moveAll(connectFour.placeDisc(column, Color.red));
                if (connectFour.checkWin(Color.red)) {
                    win(red);
                    lose(yellow);
                    isFinished = true;
                }
            } else if (user.equals(yellow)) {
                moveAll(connectFour.placeDisc(column, Color.yellow));
                if (connectFour.checkWin(Color.yellow)) {
                    win(yellow);
                    lose(red);
                    isFinished = true;
                }
            }
        }
    }

    private void win(User winner) {
        winner.win();
        System.out.println(winner.getName() + " won");
        winner.respond("GMes" + roomCode + "Win");
    }

    private void lose(User loser) {
        loser.lose();
        System.out.println(loser.getName() + " lost");
        loser.respond("GMes" + roomCode + "Lose");
    }

    public synchronized void hasJoined(User joined) {
        if (red != null && yellow != null) {
            red.respond("GMes" + roomCode + "Conn" + yellow.getName());
            yellow.respond("GMes" + roomCode + "Conn" + red.getName());
        }
        if (red != null) {
            red.respond("CMes" + roomCode + joined.getName() + " has joined!");
        }
        if (yellow != null) {
            yellow.respond("CMes" + roomCode + joined.getName() + " has joined!");
        }
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

    public synchronized void vote(User user) {
        if (user == red)
            voteRed = true;
        if (user == yellow)
            voteYellow = true;

        if (voteRed && voteYellow) {
            yellow.respond("GMes" + roomCode + "Restart");
            red.respond("GMes" + roomCode + "Restart");
            connectFour.restart();
            voteYellow = false;
            voteRed = false;
            isFinished = false;
        } else {
            yellow.respond("GMes" + roomCode + "Vote");
            red.respond("GMes" + roomCode + "Vote");
        }
    }

    public String getTurn(User user) {
        if (user.equals(red)){
            return connectFour.getStart() + "R";
        }else if (user.equals(yellow)){
            return connectFour.getStart() + "Y";
        }
        return "";
    }
}
