package client;

import client.gamelogic.Disc;
import client.gamelogic.Square;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.jfree.fx.FXGraphics2D;
import server.ConnectFour;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class GameGUI {

    private Stage stage;
    private MainMenuGUI mainMenuGUI;

    private Socket socket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket, String roomCode) {
        stage = primaryStage;
        this.mainMenuGUI = mainMenuGUI;
        this.socket = socket;

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        Label lobbyCode = new Label("Room code: " + roomCode);

        Text opponentName = new Text("Opponent: TESTNAME");

        Separator separator = new Separator();
        Separator separator2 = new Separator();

        toolBar.getItems().add(backButton);
        toolBar.getItems().add(separator2);
        toolBar.getItems().add(lobbyCode);
        toolBar.getItems().add(separator);
        toolBar.getItems().add(opponentName);

        borderPane.setTop(toolBar);

        //center pane
        discs = new ArrayList<>();
        squares = makeColumns();

        Canvas canvas = new Canvas(800, 700);
        canvas.setOnMouseClicked(event -> {
            System.out.println("X: " + event.getX() + ", Y: " + event.getY());
//            if(mouseDisabled)
            for (int i = 0; i < squares.size(); i++) {
                if (squares.get(i).getSquare().getBounds().contains(event.getX(), event.getY())) {
//                   Have to change
//                    try {
////                        dataOutputStream.writeUTF("#" + i);
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }

                    discs.add(connectFour.placeDisc(i));
                }
            }

        });
        ;

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
        });

        borderPane.setCenter(canvas);

        fxGraphics2D = new FXGraphics2D(canvas.getGraphicsContext2D());
        draw(fxGraphics2D);

        //right pane
        borderPane.setRight(setChatPane(275, 620));

        Scene scene = new Scene(borderPane, 1200, 835);
        stage.setTitle("Connect 4");
        stage.setScene(scene);
        stage.show();

        new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last == -1)
                    last = now;
                update((now - last) / 10000.0);
                last = now;
                draw(fxGraphics2D);
            }
        }.start();
    }

    private FXGraphics2D fxGraphics2D;

    private final int SQUARE_SIZE = 100;
    private final int COLUMNS = 7;
    private final int ROWS = 6;

    private ArrayList<Disc> discs;
    private ArrayList<Square> squares;

    private boolean mouseDisabled = true;

    private ConnectFour connectFour = new ConnectFour();

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
                Disc disc = new Disc(new java.awt.geom.Point2D.Double(x * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5, y * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5), Color.WHITE, SQUARE_SIZE);
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
                    null);
            squares.add(shape);
        }

        return squares;
    }

    private void update(double time) {

    }

    public BorderPane setChatPane(int width, int height) {
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
        textField.setPrefWidth(width - 80);
        HBox.setHgrow(textField, Priority.ALWAYS);

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
        inputBox.setPadding(new Insets(0, 10, 10, 10));

        borderPane.setBottom(inputBox);

        return borderPane;
    }

    private void clientGUI() {
        mainMenuGUI.start();
    }

}
