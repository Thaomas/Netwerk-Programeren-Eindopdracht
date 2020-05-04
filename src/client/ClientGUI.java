package client;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class ClientGUI extends Application {
    private Stage stage;
    private Button chatButton;
    private Button gamesButton;

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        chatButton= new Button("Chat");
        chatButton.setAlignment(Pos.CENTER);
        chatButton.setMinSize(150,100);
        gamesButton = new Button("Games");
        gamesButton.setAlignment(Pos.CENTER);
        gamesButton.setMaxSize(150,100);
        gridPane.add(chatButton,0,0);
        gridPane.add(gamesButton,1,0);
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
