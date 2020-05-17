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

public class ClientGUI {

    private final int buttonWIDTH = 150;
    private final int buttonHEIGHT = 100;
    private final String TITLE = "Connect 4";
    private Administration administration;
    private Stage stage;
    private Account account;

    public ClientGUI(Stage primaryStage, Administration administration) {
        this.stage = primaryStage;
        this.administration = administration;
        this.account = new Account();
        Runtime.getRuntime().addShutdownHook(new Thread(administration::disconnect));
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

        /*
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Width: " + newValue);
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Height: " + newValue);
        });

         */
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
        Button optionButton;

        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setPadding(new Insets(10));

        createButton = new Button("Create game");
        createButton.setAlignment(Pos.CENTER);
        createButton.setPrefSize(buttonWIDTH, buttonHEIGHT);

        joinButton = new Button("Join game");
        joinButton.setAlignment(Pos.CENTER);
        joinButton.setPrefSize(buttonWIDTH, buttonHEIGHT);

        chatButton = new Button("Chat");
        chatButton.setAlignment(Pos.CENTER);
        chatButton.setPrefSize(buttonWIDTH, buttonHEIGHT);

        optionButton = new Button("Options");
        optionButton.setAlignment(Pos.CENTER);
        optionButton.setPrefSize(buttonWIDTH, buttonHEIGHT);

        gridPane.add(createButton, 0, 0);
        gridPane.add(joinButton, 1, 0);
        gridPane.add(chatButton, 2, 0);
        gridPane.add(optionButton, 1, 1);

        return gridPane;
    }

    public void administration(){
        administration.disconnect();
        administration.start(stage);
    }

    public void account(){
        account.start(stage, this);
    }

}
