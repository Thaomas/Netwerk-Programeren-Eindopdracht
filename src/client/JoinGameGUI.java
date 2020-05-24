package client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class JoinGameGUI {

    private Stage stage;
    private ClientGUI clientGUI;
    private CreateGameGUI createGameGUI;

    public void start(Stage primaryStage, ClientGUI clientGUI){
        stage = primaryStage;
        this.clientGUI = clientGUI;

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        toolBar.getItems().add(backButton);

        borderPane.setTop(toolBar);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();

        Label title = new Label("Room");
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Label roomCode = new Label("Room code: ");
        roomCode.setAlignment(Pos.CENTER);
        roomCode.setFont(Font.font("Arial", FontWeight.LIGHT, 20));

        TextField textField = new TextField();
        HBox.setHgrow(textField, Priority.ALWAYS);

        Button join = new Button("Join game");
        join.setAlignment(Pos.CENTER);
        //TODO Make it connect to the proper game room and not create a new gameroom.
        join.setOnAction(event -> CreateGameGUI());

        hBox.getChildren().addAll(roomCode,textField,join);
        hBox.setSpacing(10);

        vBox.getChildren().addAll(title,hBox);
        vBox.setPadding(new Insets(10));

        borderPane.setCenter(vBox);

        Scene scene = new Scene(borderPane,600,200);
        stage.setTitle("Join game");
        stage.setScene(scene);
        stage.show();
    }

    private void clientGUI() {
        clientGUI.start();
    }

    public void CreateGameGUI() {
        createGameGUI = new CreateGameGUI();
        createGameGUI.start(stage, clientGUI);
    }
}
