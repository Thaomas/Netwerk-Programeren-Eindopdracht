package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatListener implements Runnable {
    private final ChatGUI chatGUI;
    private final Socket socket;
    private String roomCode;

    public ChatListener(ChatGUI gui, Socket socket, String roomCode) {
        this.chatGUI = gui;
        this.socket = socket;
        this.roomCode = roomCode;
    }

    @Override
    public void run() {
        boolean connected = true;
        DataInputStream in = null;
        try {
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Start");
        String input;
        String command;
        String roomCode;
        while (connected) {
            try {
                input = in.readUTF();
                command = input.substring(0, 4);
                roomCode = input.substring(4,8);
                if (roomCode.equals(this.roomCode)) {
                    if (command.equals("CMes")) {
                        chatGUI.addMessage(input.substring(4));

                    } else if (command.equals("Disc")) {
                        connected = false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        }
        System.out.println("Chat listener stopped");
    }
}
