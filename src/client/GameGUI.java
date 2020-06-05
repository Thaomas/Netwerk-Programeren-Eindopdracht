package client;

import client.gamelogic.Disc;
import client.gamelogic.Square;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

public class GameGUI {

    private MainMenuGUI mainMenuGUI;
    private ArrayList<String> mainChat;
    private ArrayList<String> gameChat;
    private Thread listenThread;
    private String roomCode;

    private Socket socket;

    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket, String roomCode, ArrayList<String> gameChat, ArrayList<String> mainChat) {
        this.mainMenuGUI = mainMenuGUI;
        this.socket = socket;
        this.roomCode = roomCode;
        this.mainChat = mainChat;
        this.gameChat = gameChat;
        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> backButton());

        Label lobbyCode = new Label("Room code: " + this.roomCode);

        Text opponentName = new Text("Opponent: TESTNAME");

        toolBar.getItems().add(backButton);
        toolBar.getItems().add(new Separator());
        toolBar.getItems().add(lobbyCode);
        toolBar.getItems().add(new Separator());
        toolBar.getItems().add(opponentName);

        borderPane.setTop(toolBar);

        //center pane
        discs = new ArrayList<>();
        squares = makeColumns();

        Canvas canvas = new Canvas(800, 700);

        fxGraphics2D = new FXGraphics2D(canvas.getGraphicsContext2D());


        canvas.setOnMouseClicked(event -> {

            for (int i = 0; i < squares.size(); i++) {
                if (squares.get(i).getSquare().getBounds().contains(event.getX(), event.getY())) {
                    try {
                        DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
                        dataOutputStream.writeUTF("GMes" + this.roomCode + i);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        Color transparent = new Color(255, 255, 255, 50);
        Color noColor = new Color(0, 0, 0, 0);
        canvas.setOnMouseMoved(event -> {
            for (Square square : squares) {
                if (square.getSquare().getBounds().contains(event.getX(), event.getY())) {
                    square.setColor(transparent);
                } else {
                    square.setColor(noColor);
                }
            }
            draw(fxGraphics2D);
        });

        borderPane.setCenter(canvas);
        draw(fxGraphics2D);

        //right pane
        borderPane.setRight(setChatPane(275, 620));

        Scene scene = new Scene(borderPane, 1200, 835);
        primaryStage.setTitle("Connect 4");
        primaryStage.setScene(scene);
        primaryStage.show();
        listenThread = new Thread(new GameListener(this, socket, this.roomCode));
        listenThread.start();
        draw(fxGraphics2D);
    }

    private FXGraphics2D fxGraphics2D;

    private final int SQUARE_SIZE = 100;
    private final int COLUMNS = 7;
    private final int ROWS = 6;

    private ArrayList<Disc> discs;
    private ArrayList<Square> squares;

    protected void messageToGameChat(String message) {
        if (comboBox.getSelectionModel().getSelectedItem().equals("Game chat")) {
            Platform.runLater(() -> textFlow.getChildren().add(new Text(message + "\n")));
        } else {
            gameChat.add(message);
        }
    }

    protected void messageToMainChat(String message) {
        if (comboBox.getSelectionModel().getSelectedItem().equals("Global chat")) {
            Platform.runLater(() -> textFlow.getChildren().add(new Text(message + "\n")));
        } else {
            gameChat.add(message);
        }
    }

    protected void placeDisc() {

        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Disc disc = (Disc) objectInputStream.readObject();
            if (disc != null) {
                discs.add(disc);
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            draw(fxGraphics2D);
        });
    }


    private void draw(FXGraphics2D fxGraphics2D) {
        fxGraphics2D.setBackground(Color.white);
        fxGraphics2D.clearRect(0, 0, (COLUMNS + 1) * SQUARE_SIZE, (ROWS + 1) * SQUARE_SIZE);
        fxGraphics2D.setTransform(new AffineTransform());

        makeConnect4Grid().drawFill(fxGraphics2D);

        for (Square square : squares) {
            square.drawFill(fxGraphics2D);
        }

        for (Disc disc : discs) {
            disc.draw(fxGraphics2D);
        }

    }

    public Square makeConnect4Grid() {
        Square shape = new Square(new Rectangle((COLUMNS + 1) * SQUARE_SIZE, (ROWS + 1) * SQUARE_SIZE),
                Color.blue);
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                Disc disc = new Disc(x, y, Color.WHITE, SQUARE_SIZE);
                Area areaShape = new Area(shape.getSquare());
                Area areaDisc = new Area(disc.getCircle());
                areaShape.subtract(areaDisc);
                shape.setSquare(areaShape);
            }
        }
        return shape;
    }

    private ArrayList<Square> makeColumns() {
        ArrayList<Square> squares = new ArrayList<>();

        for (int x = 0; x < COLUMNS; x++) {
            Square shape = new Square(new Rectangle2D.Double(
                    x * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5,
                    0, SQUARE_SIZE, (ROWS + 1) * SQUARE_SIZE),
                    new Color(0, 0, 0, 0));
            squares.add(shape);
        }

        return squares;
    }

    private ComboBox<String> comboBox;
    private TextFlow textFlow;

    public BorderPane setChatPane(int width, int height) {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10, 0, 0, 0));

        //Top item
        comboBox = new ComboBox<>();

        comboBox.getItems().add("Game chat");
        comboBox.getItems().add("Global chat");

        comboBox.getSelectionModel().selectFirst();
        comboBox.prefWidthProperty().bind(borderPane.widthProperty().subtract(20));
        comboBox.setTranslateX(10);
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println(comboBox.getSelectionModel().getSelectedItem());
            if (comboBox.getSelectionModel().getSelectedItem().equals("Game chat")) {
                System.out.println("Game chat");
                setChat(gameChat);
            } else if (comboBox.getSelectionModel().getSelectedItem().equals("Global chat")) {
                System.out.println("Main chat");
                setChat(mainChat);
            }
        });


        borderPane.setTop(comboBox);

        //Center items
        textFlow = new TextFlow();
        textFlow.setLineSpacing(5);
        VBox.setVgrow(textFlow, Priority.ALWAYS);
        setChat(gameChat);


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
        textField.setPrefWidth(width - 80);
        HBox.setHgrow(textField, Priority.ALWAYS);

        Button button = new Button("Send");
        button.setPrefSize(80, 30);
        button.setOnAction(e -> {
            if (!textField.getText().isEmpty() && !textField.getText().equals("")) {
                try {
                    String roomcode;
                    if (comboBox.getSelectionModel().getSelectedItem().equals("Game chat"))
                        roomcode = this.roomCode;
                    else
                        roomcode = "main";

                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    out.writeUTF("CMes" + roomcode + textField.getText());
                    System.out.println("CMes" + roomcode + textField.getText());
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
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
        inputBox.setPadding(new Insets(0, 10, 10, 10));

        borderPane.setBottom(inputBox);

        return borderPane;
    }

    private void backButton() {
        try {
            int i = 0;
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("Disc" + roomCode);
            System.out.println(i++);

            out.writeUTF("Discmain");
            System.out.println(i++);

            listenThread.join();
            System.out.println(i++);

            mainMenuGUI.start();
            System.out.println(i++);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setChat(ArrayList<String> chatLog) {
        textFlow.getChildren().clear();
        for (String message : chatLog) {
            textFlow.getChildren().add(new Text(message + "\n"));
        }
    }

}
