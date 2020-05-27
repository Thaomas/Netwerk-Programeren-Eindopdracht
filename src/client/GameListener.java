package client;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class GameListener implements Runnable {
    private TextFlow textFlow;
    private Socket socket;

    public GameListener(TextFlow textFlow, Socket socket) {
        this.textFlow = textFlow;
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
                    if (textFlow.getChildren().size() == 0) {
                        textFlow.getChildren().add(new Text(input.substring(4)));
                    } else {
                        textFlow.getChildren().add(new Text("\n" + input.substring(4)));
                    }

                } else if (command.equals("GMes")) {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
