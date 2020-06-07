package client;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import util.AlertHandler;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class JoinGameGUI {

    private Stage stage;
    private MainMenuGUI mainMenuGUI;
    private Socket socket;
    private ListView<HBox> listView;

    /**
     * Start method which sets the scene.
     *
     * @param primaryStage The class which is used to change the scene settings.
     * @param mainMenuGUI  Required for the back button. Calls upon the method start to change the scene.
     * @param socket       The class required to make connection to the server.
     */
    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket) {
        stage = primaryStage;
        this.mainMenuGUI = mainMenuGUI;
        this.socket = socket;

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> mainMenuGUI.start());

        toolBar.getItems().add(backButton);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(0, 0, 0, 13));
        hBox.prefWidthProperty().bind(toolBar.widthProperty().subtract(60));

        Label roomCodeLabel = new Label("Game code: ");
        roomCodeLabel.setAlignment(Pos.CENTER);
        roomCodeLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 20));

        TextField textField = new TextField();
        textField.setPrefWidth(92);

        Button buttonJoinGame = new Button("Join game");
        buttonJoinGame.setAlignment(Pos.CENTER);
        buttonJoinGame.setOnAction(event -> joinGameRoom(textField.getText()));

        Separator separator2 = new Separator();
        separator2.setOrientation(Orientation.VERTICAL);

        Button buttonRefreshGames = new Button("Refresh games");
        buttonRefreshGames.setOnAction(event -> setAvailableGames());

        hBox.getChildren().addAll(separator, roomCodeLabel, textField, buttonJoinGame, separator2, buttonRefreshGames);
        hBox.setSpacing(8);

        borderPane.setTop(toolBar);

        Label joinPublicGame = new Label("Public games");
        joinPublicGame.setTextAlignment(TextAlignment.CENTER);
        joinPublicGame.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        listView = new ListView<>();

        setAvailableGames();

        toolBar.getItems().add(hBox);

        vBox.getChildren().addAll(joinPublicGame, listView);
        vBox.setPadding(new Insets(10));

        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane, 500, 490);
        stage.setTitle("Join game");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets the list of available public games.
     */
    private void setAvailableGames() {
        try {
            listView.getItems().clear();
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("GLst");
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            HashMap<String, String> list = (HashMap<String, String>) objectInputStream.readObject();
            for (String roomCode : list.keySet()) {
                HBox hboxList = new HBox();
                hboxList.setSpacing(8);
                Button buttonJoin = new Button("Join");
                buttonJoin.setOnAction(event -> joinGameRoom(roomCode));
                Separator separator = new Separator();
                separator.setOrientation(Orientation.VERTICAL);
                Label roomName = new Label(list.get(roomCode));
                roomName.setFont(Font.font("Arial", 20));
                hboxList.getChildren().add(buttonJoin);
                hboxList.getChildren().add(separator);
                hboxList.getChildren().add(roomName);
                listView.getItems().add(hboxList);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to join a game room on the basis of the room code. For public games their is no need to type a room code
     * since the 'join' button is already bound to the button.
     *
     * @param roomCode String which contains the code to join the lobby.
     */
    public void joinGameRoom(String roomCode) {
        try {
            roomCode = roomCode.toLowerCase();
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF("Conn" + roomCode);
            String response = in.readUTF();

            switch (response.substring(0, 4)) {
                case "Conf":
                    String start = "YELLOW";
                    String you = "YELLOW";

                    if (response.charAt(4) == 'R')
                        start = "RED";
                    if (response.charAt(5) == 'R')
                        you = "RED";

                    ObjectInputStream inObj = new ObjectInputStream(socket.getInputStream());
                    ArrayList<String> gameChat = new ArrayList<>((ArrayList<String>) inObj.readObject());
                    inObj = new ObjectInputStream(socket.getInputStream());
                    ArrayList<String> mainChat = new ArrayList<>((ArrayList<String>) inObj.readObject());
                    new GameGUI().start(stage, mainMenuGUI, socket, roomCode, gameChat, mainChat, start, you);
                    break;
                case "Full":
                    AlertHandler.show(Alert.AlertType.ERROR, "Room Full", "Room Full", "The room you tried to join is full.");
                    break;
                case "Invl":
                    AlertHandler.show(Alert.AlertType.ERROR, "Invalid Code", "Invalid Code", "The selected room does not exist");
                    break;

            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
