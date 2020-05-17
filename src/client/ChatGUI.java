package client;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.User;

public class ChatGUI{

    private Stage stage;
    private ClientGUI clientGUI;

    private BorderPane borderPane;
    private VBox centerPane;
    private TextField input;

    public void start(Stage primaryStage, ClientGUI clientGUI){
        this.stage = primaryStage;
        this.clientGUI = clientGUI;

        borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        toolBar.getItems().add(backButton);
        borderPane.setTop(toolBar);

        centerPane = new VBox();

        borderPane.setCenter(centerPane);
        input = new TextField();

        Button enterButton = new Button("SEND");
        enterButton.setOnAction(event -> {
            updateChat();
            input.clear();
        });

        HBox bottomPane = new HBox();
        bottomPane.setSpacing(10);
        bottomPane.setPadding(new Insets(10));
        HBox.setHgrow(input, Priority.ALWAYS);
        bottomPane.getChildren().add(input);
        bottomPane.getChildren().add(enterButton);
        borderPane.setBottom(bottomPane);
        Scene scene = new Scene(borderPane,600,600);
        stage.setTitle("Chat");
        stage.setScene(scene);
        stage.show();
    }

    private void updateChat(){
        Text chat = new Text();

        chat.wrappingWidthProperty().bind(borderPane.widthProperty());
        chat.setFont(Font.font("Arial",24));
        chat.setText("You: " + input.getText());

        if(centerPane.getChildren().size() == 19){
            centerPane.getChildren().remove(0);
        }
        centerPane.getChildren().add(chat);
    }

    private void clientGUI() {
        clientGUI.start();
    }
}
