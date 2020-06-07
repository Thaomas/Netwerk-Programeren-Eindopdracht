package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.ArrayList;

public class GameRoom {
    private User red;
    private User yellow;
    private boolean voteRed;
    private boolean voteYellow;
    private boolean inProgress;
    private boolean isFinished;
    private final boolean isPrivate;
    private final String roomName;
    private final String roomCode;
    private final ConnectFour connectFour;
    private final ArrayList<String> chatlog;

    public GameRoom(String roomname, String roomcode, boolean isPrivate) {
        this.roomName = roomname;
        this.roomCode = roomcode;
        this.isPrivate = isPrivate;
        this.voteRed = false;
        this.voteYellow = false;
        this.isFinished = false;
        chatlog = new ArrayList<>();
        connectFour = new ConnectFour();
    }

    /**
     * Check if the room contains the given User.
     *
     * @param user User to check.
     * @return If the room contains the User.
     */
    public boolean containsUser(User user) {
        return red.equals(user) || yellow.equals(user);
    }

    /**
     * Add an User to the GameRoom
     *
     * @param user User to add.
     * @return If the User has been added.
     */
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

    /**
     * Removes user from a GameRoom.
     *
     * @param user User to remove.
     */
    public void removeUser(User user) {
        if (red == user || yellow == user) {
            if (red == user) {
                if (inProgress) {
                    yellow.win();
                    red.lose();
                    inProgress = false;
                    connectFour.restart();
                    yellow.respond("GMes" + roomCode + "Restart");
                }
                red = null;
            } else {
                if (inProgress) {
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

    /**
     * Gets the chatLog from the GameRoom.
     *
     * @return An Arraylist of chat messages.
     */
    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    /**
     * A check to see if there is space in the GameRoom.
     *
     * @return Boolean if there is space.
     */
    public synchronized boolean checkSpace() {
        return red == null || yellow == null;
    }

    /**
     * A move send by a User.
     *
     * @param column The chosen column.
     * @param user   The user that made the move.
     */
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

    /**
     * Messages the User that has won.
     *
     * @param winner The User that won the game.
     */
    private void win(User winner) {
        winner.win();
        winner.respond("GMes" + roomCode + "Win");
    }

    /**
     * Messages the User that has lost.
     *
     * @param loser The User that lost the game.
     */
    private void lose(User loser) {
        loser.lose();
        loser.respond("GMes" + roomCode + "Lose");
    }

    /**
     * Messages all Users that the User has joined.
     *
     * @param joined The joined User.
     */
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

    /**
     * Sends the move to all the connected users.
     *
     * @param disc The move.
     */
    private synchronized void moveAll(Disc disc) {
        red.respond("GMes" + roomCode + "Move");
        red.sendDisc(disc);
        yellow.respond("GMes" + roomCode + "Move");
        yellow.sendDisc(disc);
    }

    /**
     * Messages all the connected users the message.
     *
     * @param message The send message.
     */
    public synchronized void messageAll(String message) {
        chatlog.add(message);
        if (red != null)
            red.respond("CMes" + roomCode + message);
        if (yellow != null)
            yellow.respond("CMes" + roomCode + message);
    }

    /**
     * Vote to play another game.
     *
     * @param user User that voted.
     */
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

    /**
     * Gets which color starts first.
     *
     * @param user The user to send it to.
     * @return The color which starts first.
     */
    public String getTurn(User user) {
        if (user.equals(red)) {
            return connectFour.getStart() + "R";
        } else if (user.equals(yellow)) {
            return connectFour.getStart() + "Y";
        }
        return "";
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
}
