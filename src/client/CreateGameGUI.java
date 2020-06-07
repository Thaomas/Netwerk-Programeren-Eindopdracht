package client;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class CreateGameGUI {

    private Stage stage;
    private MainMenuGUI mainMenuGUI;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private TextField roomName;
    private CheckBox checkBox;

    /**
     * Start method which sets the scene.
     *
     * @param primaryStage The class which is used to change the scene settings.
     * @param mainMenuGUI  Required for the back button. Calls upon the method start to change the scene.
     * @param socket       The class required to make connection to the server.
     */
    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket) {
        this.stage = primaryStage;
        this.mainMenuGUI = mainMenuGUI;
        this.socket = socket;

        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> mainMenuGUI.start());

        toolBar.getItems().add(backButton);

        borderPane.setTop(toolBar);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);

        Text title = new Text("Create game");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);

        roomName = new TextField();

        gridPane.add(new Label("Room name: "), 0, 0);
        gridPane.add(roomName, 1, 0);

        checkBox = new CheckBox();

        gridPane.add(new Label("Private: "), 0, 1);
        gridPane.add(checkBox, 1, 1);

        Button createGame = new Button("Start");
        createGame.setOnAction(event -> createGame());

        gridPane.add(createGame, 1, 2);

        vBox.getChildren().add(title);
        vBox.getChildren().add(gridPane);

        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 300, 200);
        stage.setTitle("Create game");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates the scene in which the player will play the game connect 4. Calls upon the method start in GameGUI.
     */
    private void createGame() {
        try {
            String request = "CrGR";
            if (checkBox.isSelected())
                request += "p";
            else
                request += "o";
            request += roomName.getText();
            out.writeUTF(request);
            String roomCode = in.readUTF();

            out.writeUTF("Connmain");
            in.readUTF();
            ObjectInputStream inOb = new ObjectInputStream(socket.getInputStream());
            ArrayList<String> mainChat = (ArrayList<String>) inOb.readObject();

            new GameGUI().start(stage, mainMenuGUI, socket, roomCode, new ArrayList<>(), mainChat, "RED", "RED");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
