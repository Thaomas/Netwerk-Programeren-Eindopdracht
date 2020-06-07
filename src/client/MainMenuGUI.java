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

    private final LoginGUI loginGUI;

    private final Stage stage;
    private final Socket socket;

    /**
     * Constructor which initializes the variables.
     *
     * @param primaryStage The class which is used to change the scene settings.
     * @param loginGUI     Used to launch the startscreen.
     * @param socket       The class required to make connection to the server.
     */
    public MainMenuGUI(Stage primaryStage, LoginGUI loginGUI, Socket socket) {
        this.stage = primaryStage;
        this.loginGUI = loginGUI;
        this.socket = socket;
        Runtime.getRuntime().addShutdownHook(new Thread(loginGUI::disconnect));
    }

    /**
     * Method used to start the scene.
     */
    public void start() {
        BorderPane borderPane = new BorderPane();
        VBox centerPane = new VBox();

        borderPane.setTop(lobbyMenu());

        String TITLE = "Connect 4";
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

    /**
     * Method used to create the bar situated at the top of the scene.
     *
     * @return The MenuBar Node.
     */
    public MenuBar lobbyMenu() {
        MenuBar menubar = new MenuBar();

        Menu accountMenu = new Menu("Account");

        MenuItem preferences = new MenuItem("Preferences");
        preferences.setOnAction(e -> new AccountGUI().start(stage, this, loginGUI, socket));

        MenuItem signOut = new MenuItem("Sign Out");
        signOut.setOnAction(e -> {
            loginGUI.disconnect();
            loginGUI.start(stage);
        });

        accountMenu.getItems().addAll(preferences, signOut);

        menubar.getMenus().add(accountMenu);

        return menubar;
    }

    /**
     * Creates a GridPane with all the relevant buttons which directs the user to different scenes.
     *
     * @return Gridpane with all relevant data.
     */
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
        int buttonWIDTH = 150;
        int buttonHEIGHT = 100;
        createButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        createButton.setOnAction(event -> new CreateGameGUI().start(stage, this, socket));

        joinButton = new Button("Join game");
        joinButton.setAlignment(Pos.CENTER);
        joinButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        joinButton.setOnAction(event -> new JoinGameGUI().start(stage, this, socket));

        chatButton = new Button("Chat");
        chatButton.setAlignment(Pos.CENTER);
        chatButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        chatButton.setOnAction(event -> ChatGUI());

        leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setAlignment(Pos.CENTER);
        leaderboardButton.setPrefSize(buttonWIDTH, buttonHEIGHT);
        leaderboardButton.setOnAction(event -> new LeaderboardGUI().start(stage, this, socket));

        gridPane.add(createButton, 0, 0);
        gridPane.add(joinButton, 1, 0);
        gridPane.add(chatButton, 2, 0);
        gridPane.add(leaderboardButton, 1, 1);

        return gridPane;
    }

    /**
     * Method used to join the "global chat".
     */
    public void ChatGUI() {
        try {

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out.writeUTF("Connmain");
            String message = in.readUTF();
            if (message.equals("Conf")) {
                ObjectInputStream inOb = new ObjectInputStream(socket.getInputStream());
                ArrayList<String> chatlog = (ArrayList<String>) inOb.readObject();

                new ChatGUI().start(stage, this, socket, chatlog);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
