package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatListener implements Runnable {
    private final ChatGUI chatGUI;
    private final Socket socket;

    public ChatListener(ChatGUI gui, Socket socket) {
        this.chatGUI = gui;
        this.socket = socket;
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
        String input;
        String command;
        while (connected) {
            try {
                input = in.readUTF();
                command = input.substring(0, 4);
                if (command.equals("CMes")) {
                    chatGUI.addMessage(input.substring(4));

                }else if (command.equals("Disc")){
                    connected = false;
                }
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        }
        System.out.println("Chat listener stopped");
    }
}
