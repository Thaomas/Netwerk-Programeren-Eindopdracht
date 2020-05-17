package client;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ChatGUI {

    private ClientGUI clientGUI;
    private BorderPane borderPane;

    public void start(Stage primaryStage, ClientGUI clientGUI) {
        this.clientGUI = clientGUI;

        borderPane = chatBox(400, 520);

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        toolBar.getItems().add(backButton);
        borderPane.setTop(toolBar);

        Scene scene = new Scene(borderPane, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ChatBox");
        primaryStage.show();
    }

    public BorderPane chatBox(int width, int height) {
        BorderPane borderPane = new BorderPane();

        ScrollPane scrollPane = new ScrollPane();

        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(10);
        TextField textField = new TextField();
        textField.setPrefHeight(30);
        Button button = new Button("Send");
        button.setPrefSize(80, 30);
        VBox centerTextBox = new VBox();
        centerTextBox.setPadding(new Insets(10));
        centerTextBox.getChildren().addAll(scrollPane, textFlow);

        borderPane.setCenter(centerTextBox);

        HBox inputBox = new HBox(textField, button);
        inputBox.setPadding(new Insets(0,10,10,10));
        HBox.setHgrow(textField,Priority.ALWAYS);

        borderPane.setBottom(inputBox);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(textFlow, Priority.ALWAYS);
        textField.setPrefWidth(width-80);

        textField.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                button.fire();
            }
        });

        button.setOnAction(e -> {
            Text text;
            if (!textField.getText().isEmpty() || !textField.getText().equals(" ")) {
                if (textFlow.getChildren().size() == 0) {
                    text = new Text("You: " + textField.getText());
                } else {
                    text = new Text("\n" + "You: " + textField.getText());
                }
                textFlow.getChildren().add(text);
                textField.clear();
                textField.requestFocus();
            }
        });
        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow);
        scrollPane.setVmax(width + 40);
        scrollPane.setPrefSize(width, height);
        scrollPane.setContent(vBox);
        scrollPane.vvalueProperty().bind(vBox.heightProperty());

        return borderPane;
    }

    private void clientGUI() {
        clientGUI.start();
    }
}
