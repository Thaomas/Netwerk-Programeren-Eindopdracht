package client;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import util.RandomString;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

public class JoinGameGUI {

    //needs to be server side
    private final RandomString randomString = new RandomString(4);

    private Stage stage;
    private MainMenuGUI mainMenuGUI;
    private Socket socket;
    private String roomCode;

    //Has to ask server if room is full.

    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket){
        stage = primaryStage;
        this.mainMenuGUI = mainMenuGUI;
        this.socket = socket;
        roomCode = randomString.nextString();

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        toolBar.getItems().add(backButton);

        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(0,0,0,13));
        hBox.prefWidthProperty().bind(toolBar.widthProperty().subtract(60));

        Label roomCodeLabel = new Label("Game code: ");
        roomCodeLabel.setAlignment(Pos.CENTER);
        roomCodeLabel.setFont(Font.font("Arial", FontWeight.LIGHT, 20));

        TextField textField = new TextField();
        HBox.setHgrow(textField, Priority.ALWAYS);

        Button buttonJoinGame = new Button("Join game");
        buttonJoinGame.setAlignment(Pos.CENTER);
        //TODO Make it connect to the proper game room and not create a new gameroom.
        buttonJoinGame.setOnAction(event -> CreateGameGUI());

        hBox.getChildren().addAll(separator,roomCodeLabel,textField,buttonJoinGame);
        hBox.setSpacing(8);

        borderPane.setTop(toolBar);

        Label joinPublicGame = new Label("Public games");
        joinPublicGame.setTextAlignment(TextAlignment.CENTER);
        joinPublicGame.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        ListView<HBox> listView = new ListView<>();
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF("GLst");
            //TODO
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            HashMap<String, String> list = (HashMap<String,String>) objectInputStream.readObject();
            for (String roomCode : list.keySet()) {
                HBox hboxList = new HBox();
                hboxList.setSpacing(8);
                Button buttonJoin = new Button("Join");
                separator = new Separator();
                separator.setOrientation(Orientation.VERTICAL);
                Label roomName = new Label(list.get(roomCode) + " " + roomCode);
                roomName.setFont(Font.font("Arial",20));
                hboxList.getChildren().add(buttonJoin);
                hboxList.getChildren().add(separator);
                hboxList.getChildren().add(roomName);
                listView.getItems().add(hboxList);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        toolBar.getItems().add(hBox);

        vBox.getChildren().addAll(joinPublicGame,listView);
        vBox.setPadding(new Insets(10));

        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane,500,490);
        stage.setTitle("Join game");
        stage.setScene(scene);
        stage.show();
    }

    private void clientGUI() {
        mainMenuGUI.start();
    }

    public void CreateGameGUI() {
//        GameGUI gameGUI = new GameGUI();
//        gameGUI.start(stage, mainMenuGUI,socket,roomCode);
    }
}
