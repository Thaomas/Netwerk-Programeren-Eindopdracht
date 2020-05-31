package client;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class GameListener implements Runnable {
    private final GameGUI gameGui;
    private final Socket socket;
    private final String roomCode;

    public GameListener(GameGUI gameGUI, Socket socket, String roomCode) {
        this.gameGui = gameGUI;
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
        String input;
        String command;
        String roomCode;
        while (connected) {
            try {
                input = in.readUTF();
                if (input.equals("quit")){
                    connected = false;
                    continue;
                }
                command = input.substring(0, 4);
                roomCode = input.substring(4, 8);
                if (roomCode.equals(this.roomCode)) {
                    if (command.equals("CMes")) {
                        gameGui.messageToGameChat(input.substring(8));
                    } else if (command.equals("GMes")) {

                    }
                }else if (roomCode.equals("main") && command.equals("CMes")){
                    gameGui.messageToMainChat(input.substring(8));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
