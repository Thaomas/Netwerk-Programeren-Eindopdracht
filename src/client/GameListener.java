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
        while (connected) {
            try {
                String input = in.readUTF();
                if (input.equals("Disc")) {
                    connected = false;
                    continue;
                }
                String command = input.substring(0, 4);
                String roomCode = input.substring(4, 8);
                if (command.equals("CMes")) {
                    if (roomCode.equals(this.roomCode))
                        gameGui.messageToGameChat(input.substring(8));
                    else if (roomCode.equals("main"))
                        gameGui.messageToMainChat(input.substring(8));
                } else if (command.equals("GMes")) {
                    if (roomCode.equals(this.roomCode)) {
                        if (input.substring(8).contains("Conn")) {
                            gameGui.setOpponentName(input.substring(12));
                        } else {
                            switch (input.substring(8)) {
                                case "Move":
                                    gameGui.placeDisc();
                                    break;
                                case "Win":
                                    gameGui.restartGame(input.substring(8));
                                    break;
                                case "Lose":
                                    gameGui.restartGame(input.substring(8));
                                    break;
                                case "Vote":
                                    gameGui.vote();
                                    break;
                                case "Restart":
                                    gameGui.restart();
                                    break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        }
    }
}
