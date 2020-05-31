package client;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ChatGUI {

    private MainMenuGUI mainMenuGUI;
    private DataOutputStream out;
    private String roomCode;
    private TextFlow textFlow;
    private Thread listenThread;

    public void start(String roomCode, Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket, ArrayList<String> chatLog) {
        this.mainMenuGUI = mainMenuGUI;
        this.roomCode = roomCode;

        try {
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        BorderPane borderPane = chatBox(socket, chatLog);

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            try {
                out.writeUTF("Disc"+this.roomCode);
                listenThread.join();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            clientGUI();
        });

        toolBar.getItems().add(backButton);
        borderPane.setTop(toolBar);

        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Global chat");
        primaryStage.show();
    }
    private BorderPane chatBox(Socket socket, ArrayList<String> chatlog) {
        BorderPane borderPane = new BorderPane();

        //Center items
        textFlow = new TextFlow();
        textFlow.setLineSpacing(5);
        VBox.setVgrow(textFlow, Priority.ALWAYS);

        for (String message : chatlog){
            if (textFlow.getChildren().size()>0)
                message = "\n" + message;
            textFlow.getChildren().add(new Text(message));
        }

        VBox vBox = new VBox();
        vBox.getChildren().add(textFlow);

        ScrollPane scrollPane = new ScrollPane();
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        scrollPane.setVmax(400 + 40);
        scrollPane.setPrefSize(400, 520);
        scrollPane.setContent(vBox);
        scrollPane.vvalueProperty().bind(vBox.heightProperty());

        VBox centerTextBox = new VBox();
        centerTextBox.setPadding(new Insets(10));
        centerTextBox.getChildren().add(scrollPane);

        borderPane.setCenter(centerTextBox);

        //Bottom items
        TextField chatTextField = new TextField();
        chatTextField.setPrefHeight(30);
        chatTextField.setPrefWidth(320);
        HBox.setHgrow(chatTextField, Priority.ALWAYS);

        Button sendButton = new Button("Send");
        sendButton.setPrefSize(80, 30);
        sendButton.setOnAction(e -> {
            if (!chatTextField.getText().isEmpty() && !chatTextField.getText().equals("")) {
                try {
                    out.writeUTF("CMes" + this.roomCode +chatTextField.getText());
                    System.out.println("CMes" + this.roomCode +chatTextField.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                chatTextField.clear();
                chatTextField.requestFocus();
            }
        });

        chatTextField.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                sendButton.fire();
            }
        });

        ChatListener listener = new ChatListener(this, socket);
        listenThread = new Thread(listener);
        listenThread.start();

        HBox inputBox = new HBox(chatTextField, sendButton);
        inputBox.setPadding(new Insets(0, 10, 10, 10));

        borderPane.setBottom(inputBox);

        return borderPane;
    }
    protected void addMessage(String message){
        Platform.runLater(()-> textFlow.getChildren().add(new Text("\n"+message)));
    }

     private void clientGUI() {
        mainMenuGUI.start();
    }

}
