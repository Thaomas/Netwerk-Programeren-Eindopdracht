package client;

import client.gamelogic.Disc;
import client.gamelogic.Square;
import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
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
import javafx.util.Duration;
import org.jfree.fx.FXGraphics2D;
import util.RandomString;

//import java.awt.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
//import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javafx.geometry.Point2D;

public class CreateGameGUI {

    private Stage stage;
    private ClientGUI clientGUI;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    //When creating a game. The client has to ask the server for a room code.
    //When an opponent connect it has to update the opponents name.
    //When an opponent disconnects it has to update the opponents name to nothing.

    public void start(Stage primaryStage, ClientGUI clientGUI) {
        stage = primaryStage;
        this.clientGUI = clientGUI;

        BorderPane borderPane = new BorderPane();

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

        //center pane
        discs = new ArrayList<>();
//        squares = new ArrayList<>();
        squares = makeColumns();

        canvas = new Canvas(800, 700);
        canvas.setOnMouseClicked(event -> {
//            addDisc(event.getX(), event.getY());
            System.out.println("X: " + event.getX() + ", Y: " + event.getY());
            for (int i = 0; i < squares.size(); i++) {
                if (squares.get(i).getSquare().getBounds().contains(event.getX(), event.getY())) {
                    //Doesnt work
                    placeDisc(i);
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
        });

        borderPane.setCenter(canvas);

        fxGraphics2D = new FXGraphics2D(canvas.getGraphicsContext2D());
        draw(fxGraphics2D);

        //right pane
        borderPane.setRight(setChatPane(275, 620));

        Scene scene = new Scene(borderPane, 1200, 835);
        stage.setTitle("Create game");
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

    private Canvas canvas;
    private FXGraphics2D fxGraphics2D;

    private final int SQUARE_SIZE = 100;
    private final int COLUMNS = 7;
    private final int ROWS = 6;

    private ArrayList<Disc> discs;
    private ArrayList<Square> squares;

    private Disc[][] grid = new Disc[COLUMNS][ROWS];

    private boolean redMove = true;

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
            Square shape = new Square(new Rectangle2D.Double(x * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5, 0, SQUARE_SIZE, (ROWS + 1) * SQUARE_SIZE),
                    null);
            squares.add(shape);
        }

        return squares;
    }

    private void placeDisc(int column) {
        int row = ROWS - 1;


        while (row >= 0) {
            if (!getDisc(column, row).isPresent())
                break;

            System.out.println("test row:" + row);
            row--;
        }

        if (row < 0)
            return;

        Disc disc = new Disc(new java.awt.geom.Point2D.Double(
                column * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5,
                row * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5), Color.red, SQUARE_SIZE);

        if (redMove) {
            disc.setColor(Color.red);
        } else {
            disc.setColor(Color.yellow);
        }

        redMove = !redMove;
        grid[column][row] = disc;
        discs.add(disc);

//        if(gameEnded(column,row)){
//            gameOver();
//        }

        System.out.println("Disc column: " + column + ", row: " + row);

    }

    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= COLUMNS
                || row < 0 || row >= ROWS)
            return Optional.empty();

        return Optional.ofNullable(grid[column][row]);
    }

//    Doesnt work (TODO)
//    //dont use
//    private boolean gameEnded(int column, int row) {
//        ArrayList<Point2D> vertical = (ArrayList<Point2D>) IntStream.rangeClosed(row - 3, row + 3)
//                .mapToObj(r -> new Point2D(column, r))
//                .collect(Collectors.toList());
//
//        ArrayList<Point2D> horizontal = (ArrayList<Point2D>) IntStream.rangeClosed(column - 3, column + 3)
//                .mapToObj(c -> new Point2D(c, row))
//                .collect(Collectors.toList());
//
//        Point2D topLeft = new Point2D(column - 3, row - 3);
//        ArrayList<Point2D> diagonal1 = (ArrayList<Point2D>) IntStream.rangeClosed(0, 6)
//                .mapToObj(i -> topLeft.add(i, i))
//                .collect(Collectors.toList());
//
//        Point2D botLeft = new Point2D(column - 3, row + 3);
//        ArrayList<Point2D> diagonal2 = (ArrayList<Point2D>) IntStream.rangeClosed(0, 6)
//                .mapToObj(i -> botLeft.add(i, -i))
//                .collect(Collectors.toList());
//
//        return checkRange(vertical) || checkRange(horizontal)
//                || checkRange(diagonal1) || checkRange(diagonal2);
//    }
//
//    //dont use
//    private boolean checkRange(ArrayList<Point2D> points) {
//        int chain = 0;
//
//        for (Point2D p : points) {
//            int column = (int) p.getX();
//            int row = (int) p.getY();
//
//            Disc disc = getDisc(column, row).orElse(
//                    new Disc(new java.awt.geom.Point2D.Double(0, 0), Color.WHITE, SQUARE_SIZE));
//            if (redMove) {
//                chain++;
//                if (chain == 4) {
//                    return true;
//                }
//            } else {
//                chain = 0;
//            }
//        }
//
//        return false;
//    }
//
//    //dont use
//    private void gameOver() {
//        System.out.println("Winner: " + (redMove ? "RED" : "YELLOW"));
//    }

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
