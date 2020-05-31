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
import util.RandomString;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class CreateGameGUI {

    //needs to be server side
    private final RandomString randomString = new RandomString(4);

    private Stage stage;
    private ClientGUI clientGUI;
    private Socket socket;
    private String roomCode;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean isPrivate;
    private TextField roomName;

    public void start(Stage primaryStage, ClientGUI clientGUI, Socket socket) {
        this.stage = primaryStage;
        this.clientGUI = clientGUI;
        this.socket = socket;
        this.roomCode = randomString.nextString();

        try {
            this.out = new DataOutputStream(socket.getOutputStream());
            this.in = new DataInputStream(socket.getInputStream());
//            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
//            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

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

        CheckBox checkBox = new CheckBox();
        isPrivate = false;
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> isPrivate = checkBox.isSelected());

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

    private void createGame() {
        try {
            String request = "CrGR";
            if (isPrivate)
                request += "p";
            else
                request += "o";
            request += roomName.getText();
            out.writeUTF(request);
            roomCode = in.readUTF();
            gameGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameGUI() {
        new GameGUI().start(stage, clientGUI, socket, roomCode);
    }

    private void clientGUI() {
        clientGUI.start();
    }

}
