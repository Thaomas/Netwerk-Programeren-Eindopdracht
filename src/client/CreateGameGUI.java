package client;

import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.RandomString;

import java.util.concurrent.atomic.AtomicInteger;

public class CreateGameGUI {

    private Stage stage;
    private ClientGUI clientGUI;

    private BorderPane borderPane;
    private BorderPane rightPane;
    private GridPane gridPane;
    private VBox chatPane;
    private TextField input;

    public void start(Stage primaryStage, ClientGUI clientGUI){
        stage = primaryStage;
        this.clientGUI = clientGUI;

        borderPane = new BorderPane();
        gridPane = new GridPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        RandomString randomString = new RandomString(4);

        Label lobbyCode = new Label("Room code: " + randomString.nextString());

        Text opponentName = new Text("Opponent: adsdgasdgsdgdg");

        Separator separator = new Separator();
        Separator separator2 = new Separator();

        toolBar.getItems().add(backButton);
        toolBar.getItems().add(separator2);
        toolBar.getItems().add(lobbyCode);
        toolBar.getItems().add(separator);
        toolBar.getItems().add(opponentName);

        borderPane.setTop(toolBar);

        AtomicInteger counter = new AtomicInteger();
        for (int y = 0; y < 7; y++) {
            for (int x = 0; x < 6; x++) {
                Image image = new Image("\\white.png");
                ImageView imageView = new ImageView(image);
                imageView.setId(x+""+y);
                gridPane.add(imageView,y,x);

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
                    gridPane.add(imageView2,height,length);

                    counter.getAndIncrement();
                    System.out.println(counter);
                });
                gridPane.add(button,y,x);
            }
        }

        borderPane.setCenter(gridPane);

//        BorderPane rightPane = new BorderPane();
//        rightPane.setPadding(new Insets(10));
//
//        chatPane = new VBox();
//        chatPane.setBorder(new Border(new BorderStroke(Color.BLACK,
//                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
//        chatPane.setSpacing(10);
//
//        HBox messagePane = new HBox();
//
//        input = new TextField();
//
//        Button enterButton = new Button("SEND");
//        enterButton.setOnAction(event -> {
//            updateChat();
//            input.clear();
//        });
//
//        messagePane.getChildren().add(input);
//        messagePane.getChildren().add(enterButton);
//
//        rightPane.setCenter(chatPane);
//        rightPane.setBottom(messagePane);

        ChatGUI test = new ChatGUI();

        BorderPane rightPane = test.chatBox(275,620);
        borderPane.setRight(rightPane);

        Scene scene = new Scene(borderPane,1000,635);
        stage.setTitle("Create game");
        stage.setScene(scene);
        stage.show();
    }

    private void updateChat(){
        Text chat = new Text();

        chat.setWrappingWidth(190);
        chat.setFont(Font.font("Arial",24));
        chat.setText("You: " + input.getText());

        if(chatPane.getChildren().size() == 10){
            chatPane.getChildren().remove(0);
        }
        chatPane.getChildren().add(chat);
    }

    private void clientGUI() {
        clientGUI.start();
    }
}
