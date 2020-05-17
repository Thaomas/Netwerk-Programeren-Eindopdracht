package client;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import util.RandomString;

import java.util.concurrent.atomic.AtomicInteger;

public class CreateGameGUI {

    private Stage stage;
    private ClientGUI clientGUI;

    private BorderPane borderPane;
    private GridPane gridPane;

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

        toolBar.getItems().add(backButton);
        toolBar.getItems().add(lobbyCode);
        borderPane.setTop(toolBar);

//        Image image = new Image(new FileInputStream(new File("\\res\\red.png")));
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

        Scene scene = new Scene(borderPane,700,635);
        stage.setTitle("Create game");
        stage.setScene(scene);
        stage.show();
    }

    private void clientGUI() {
        clientGUI.start();
    }
}
