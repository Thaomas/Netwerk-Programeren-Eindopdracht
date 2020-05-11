package client;

import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import server.User;

public class ChatGUI {
    private final Stage stage;
    public ChatGUI(Stage stage) {
        this.stage = stage;
        start();
    }

    private Text chat;

    private void start(){
        BorderPane borderPane = new BorderPane();
        chat = new Text();
        borderPane.setCenter(chat);
        TextField input = new TextField();
        borderPane.setBottom(input);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();
    }

    private void updateChat(){
        chat.setText(chat.getText());
    }


}
