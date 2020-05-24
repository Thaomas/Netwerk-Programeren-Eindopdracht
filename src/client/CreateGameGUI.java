package client;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import util.RandomString;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class CreateGameGUI {

    private Stage stage;
    private ClientGUI clientGUI;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public void start(Stage primaryStage, ClientGUI clientGUI){
        stage = primaryStage;
        this.clientGUI = clientGUI;

        BorderPane borderPane = new BorderPane();
        GridPane gridPane = new GridPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        RandomString randomString = new RandomString(4);

        Label lobbyCode = new Label("Room code: " + randomString.nextString());

        Text opponentName = new Text("Opponent: TESTNAME");

        Separator separator = new Separator();
        Separator separator2 = new Separator();

        toolBar.getItems().add(backButton);
        toolBar.getItems().add(separator2);
        toolBar.getItems().add(lobbyCode);
        toolBar.getItems().add(separator);
        toolBar.getItems().add(opponentName);

        borderPane.setTop(toolBar);

        AtomicInteger counter = new AtomicInteger();
        for (int x = 0; x < 7; x++) {
            for (int y = 0; y < 6; y++) {
                Image image = new Image("\\white.png");
                ImageView imageView = new ImageView(image);
                imageView.setId(x+""+y);
                gridPane.add(imageView,x,y);


                Button button = new Button();
                button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
                button.setOpacity(0);

                final int height = y;
                final int length = x;
                button.setOnAction(event -> {
                    System.out.println("Height: " + height + ", Length: " + length);
                    Image image2;
                    int index = counter.get();
                    //TODO Wont work if online where it has to update on both sides
                    if(index%2==0){
                        image2 = new Image("\\red.png");
                    }else {
                        image2 = new Image("\\yellow.png");
                    }
                    ImageView imageView2 = new ImageView(image2);
                    gridPane.add(imageView2,length,height);

                    counter.getAndIncrement();


                });
                gridPane.add(button,x,y);
            }
        }

        borderPane.setCenter(gridPane);

        borderPane.setRight(setChatPane(275,620));

        Scene scene = new Scene(borderPane,1000,635);
        stage.setTitle("Create game");
        stage.setScene(scene);
        stage.show();
    }

    public BorderPane setChatPane(int width, int height){
        BorderPane borderPane = new BorderPane();

        //Center items
        TextFlow textFlow = new TextFlow();
        textFlow.setLineSpacing(10);
        VBox.setVgrow(textFlow, Priority.ALWAYS);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(textFlow);

        ScrollPane scrollPane = new ScrollPane();
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        scrollPane.setVmax(width + 40);
        scrollPane.setPrefSize(width, height);
        scrollPane.setContent(vBox);
        scrollPane.vvalueProperty().bind(vBox.heightProperty());

        VBox centerTextBox = new VBox();
        centerTextBox.setPadding(new Insets(10));
        centerTextBox.getChildren().addAll(scrollPane);

        borderPane.setCenter(centerTextBox);

        //Bottom items
        TextField textField = new TextField();
        textField.setPrefHeight(30);
        textField.setPrefWidth(width-80);
        HBox.setHgrow(textField,Priority.ALWAYS);

        Button button = new Button("Send");
        button.setPrefSize(80, 30);
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

        textField.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                button.fire();
            }
        });

        HBox inputBox = new HBox(textField, button);
        inputBox.setPadding(new Insets(0,10,10,10));

        borderPane.setBottom(inputBox);

        return borderPane;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
        try {
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientGUI() {
        clientGUI.start();
    }
    
}
