package server;

import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;

public class ChatRoom {
    private final String roomName;
    private final String roomCode;
    private final ArrayList<String> chatlog;
    private final HashSet<User> users;

    public ChatRoom(String roomname, String roomcode) {
        this.roomName = roomname;
        this.roomCode = roomcode;
        users = new HashSet<>();
        chatlog = new ArrayList<>();
    }

    public ChatRoom(String roomName, String roomCode, ArrayList<String> chatlog) {
        this.roomName = roomName;
        this.roomCode = roomCode;
        this.chatlog = chatlog;
        this.users = new HashSet<>();
    }
    /**
     * Get the Json object to save.
     * @return Json Object
     */
    public JSONObject getJSON() {
        JSONObject object = new JSONObject();
        object.put("roomName", this.roomName);
        object.put("roomCode", this.roomCode);
        object.put("chatlog", this.chatlog);
        return object;
    }

    /**
     * Gets the code of the ChatRoom.
     * @return The code of the ChatRoom.
     */
    public String getRoomCode() {
        return roomCode;
    }

    /**
     * Adds an User to the ChatRoom.
     * @param user User to be added.
     */
    public void addUser(User user) {
        users.add(user);
    }

    /**
     * Removes a User from the ChatRoom.
     * @param user User to be removed.
     */
    public void removeUser(User user) {
        users.remove(user);
    }

    /**
     * Get the chatLog of the room.
     * @return An Arraylist of messages.
     */
    public ArrayList<String> getChatLog() {
        return this.chatlog;
    }

    /**
     * Messages all connected Users the message.
     * @param message Message to send to all connected Users.
     */
    public void messageAll(String message) {
        chatlog.add(message);
        for (User user : users) {
            user.respond("CMes" + roomCode + message);
        }
    }
}
