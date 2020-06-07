package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MainMenuGUI {

    private final int buttonWIDTH = 150;
    private final int buttonHEIGHT = 100;
    private final String TITLE = "Connect 4";

    private LoginGUI loginGUI;

    private AccountGUI accountGUI;
    private ChatGUI chatGUI;
    private CreateGameGUI createGameGUI;
    private JoinGameGUI joinGameGUI;

    private Stage stage;
    private Socket socket;

    public MainMenuGUI(Stage primaryStage, LoginGUI loginGUI, Socket socket) {
        this.stage = primaryStage;
        this.loginGUI = loginGUI;
        this.accountGUI = new AccountGUI();
        this.chatGUI = new ChatGUI();

        joinGameGUI = new JoinGameGUI();
        this.socket = socket;
        Runtime.getRuntime().addShutdownHook(new Thread(loginGUI::disconnect));
    }

    public void start() {
        BorderPane borderPane = new BorderPane();
        VBox centerPane = new VBox();

        borderPane.setTop(lobbyMenu());

        Text title = new Text(TITLE);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        centerPane.getChildren().add(title);
        centerPane.getChildren().add(lobbyButtons());
        centerPane.setAlignment(Pos.CENTER);

        borderPane.setCenter(centerPane);

        Scene scene = new Scene(borderPane, 520, 360);

        stage.setTitle(TITLE);
        stage.setScene(scene);
    }

    public MenuBar lobbyMenu() {
        MenuBar menubar = new MenuBar();

        Menu accountMenu = new Menu("Account");

        MenuItem preferences = new MenuItem("Preferences");
        preferences.setOnAction(e -> account());

        MenuItem signOut = new MenuItem("Sign Out");
        signOut.setOnAction(e -> administration());

        accountMenu.getItems().addAll(preferences, signOut);

        menubar.getMenus().add(accountMenu);

        return menubar;
    }

    public GridPane lobbyButtons() {
        GridPane gridPane = new GridPane();
        Button createButton;
        Button joinButton;
        Button chatButton;
        Button leaderboardButton;

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));

        createButton = new Button("Create game");
        createButton.setAlignment(Pos.CENTER);
        createButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        createButton.setOnAction(event -> CreateGameGUI());

        joinButton = new Button("Join game");
        joinButton.setAlignment(Pos.CENTER);
        joinButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        joinButton.setOnAction(event -> JoinGameGUI());

        chatButton = new Button("Chat");
        chatButton.setAlignment(Pos.CENTER);
        chatButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        chatButton.setOnAction(event -> ChatGUI());

        leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setAlignment(Pos.CENTER);
        leaderboardButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        leaderboardButton.setOnAction(event -> leaderboardGUI());

        gridPane.add(createButton, 0, 0);
        gridPane.add(joinButton, 1, 0);
        gridPane.add(chatButton, 2, 0);
        gridPane.add(leaderboardButton, 1, 1);

        return gridPane;
    }

    public void administration() {
        loginGUI.disconnect();
        loginGUI.start(stage);
    }

    public void account() {
        accountGUI.start(stage, this, loginGUI);
    }

    protected Socket getSocket() {
        return this.socket;
    }

    public void ChatGUI() {
        try {
            String roomCode = "main";
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF("Conn" + roomCode);
            String message = in.readUTF();
            if (message.equals("Conf")) {
                ObjectInputStream inOb = new ObjectInputStream(socket.getInputStream());
                ArrayList<String> chatlog = (ArrayList<String>) inOb.readObject();
                chatGUI.start(roomCode, stage, this, socket, chatlog);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void CreateGameGUI() {
        createGameGUI = new CreateGameGUI();
        createGameGUI.start(stage, this, socket);
    }

    public void JoinGameGUI() {
        joinGameGUI.start(stage, this, socket);
    }

    public void leaderboardGUI() {
        LeaderboardGUI leaderboardGUI = new LeaderboardGUI();
        leaderboardGUI.start(stage, this, socket);
    }
}
