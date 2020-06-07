package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatListener implements Runnable {
    private final ChatGUI chatGUI;
    private final Socket socket;
    private final String roomCode;

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
        while (connected) {
            try {
                String input = in.readUTF();
                String command = input.substring(0, 4);
                if (command.equals("Disc")) {
                    connected = false;
                    continue;
                }
                String roomCode = input.substring(4, 8);
                if (roomCode.equals(this.roomCode)) {
                    if (command.equals("CMes")) {
                        chatGUI.addMessage(input.substring(8));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        }
    }
}
