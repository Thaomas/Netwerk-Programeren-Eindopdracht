package client;

import com.sun.security.ntlm.Client;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.time.LocalDate;
import java.util.Scanner;

public class Account {

    private String username;
    private String password;
    private LocalDate accountCreated;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private Socket socket;

    private TextField oldPass;
    private TextField newPass;
    private TextField newPassConfirm;
    private ClientGUI clientGUI;

    public final void testData() {
        this.username = "'Username'";
        this.password = "test";
        this.accountCreated = LocalDate.now();
        this.gamesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
    }

    private DataOutputStream out;
    private DataInputStream in;

    private void getData() {
        try {
            socket = clientGUI.getSocket();
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeUTF("GUsD");
            String response = in.readUTF();
            Scanner reader = new Scanner(response);
            reader.useDelimiter("/");

            username = reader.next();
            gamesPlayed = Integer.parseInt(reader.next());
            wins = Integer.parseInt(reader.next());
            losses = gamesPlayed- wins;
            accountCreated = LocalDate.parse(reader.next());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(Stage primaryStage, ClientGUI clientGUI) {
        this.clientGUI = clientGUI;

        getData();

        BorderPane borderPane = new BorderPane();
        ToolBar toolBar = new ToolBar();
        GridPane credentials = new GridPane();
        VBox centerPane = new VBox();
        VBox statsBox = new VBox();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> clientGUI());

        toolBar.getItems().add(backButton);

        credentials.setVgap(10);
        credentials.setHgap(10);
        credentials.setPadding(new Insets(10));

        credentials.add(new Label("Old password: "), 0, 0);
        oldPass = new PasswordField();
        credentials.add(oldPass, 1, 0);

        Label visible = new Label("View password?");
        CheckBox viewBox = new CheckBox();
        viewBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String textOldPass = oldPass.getText();
            String textNewPass = newPass.getText();
            String textNewPassConfirm = newPassConfirm.getText();
            if (viewBox.isSelected()) {
                oldPass = new TextField();
                newPass = new TextField();
                newPassConfirm = new TextField();
            } else {
                oldPass = new PasswordField();
                newPass = new PasswordField();
                newPassConfirm = new PasswordField();
            }
            oldPass.setText(textOldPass);
            newPass.setText(textNewPass);
            newPassConfirm.setText(textNewPassConfirm);
            credentials.add(oldPass, 1, 0);
            credentials.add(newPass, 1, 1);
            credentials.add(newPassConfirm, 1, 2);
        });
        HBox visiblePassword = new HBox(viewBox, visible);
        credentials.add(visiblePassword, 2, 0);

        credentials.add(new Label("New password: "), 0, 1);
        newPass = new PasswordField();
        credentials.add(newPass, 1, 1);

        credentials.add(new Label("New password \n confirmation: "), 0, 2);
        newPassConfirm = new PasswordField();
        credentials.add(newPassConfirm, 1, 2);

        Button changePassButton = new Button("Change password");
        credentials.add(changePassButton, 1, 3);
        changePassButton.setOnAction(e->{
            try {
                out.writeUTF("ChPw"+oldPass.getText()+"|"+newPass.getText()+"|"+newPassConfirm.getText());
                String response = in.readUTF();
                if (response.equals("Conf")){
                    oldPass.clear();
                    newPass.clear();
                    newPassConfirm.clear();
                }else {

                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });


        Text title = new Text(username + " Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTranslateX(10);
        centerPane.getChildren().add(title);
        centerPane.getChildren().add(credentials);
        centerPane.setAlignment(Pos.CENTER_LEFT);

        Label statistics = new Label("Stats");
        statistics.setFont(Font.font("Arial", FontPosture.ITALIC, 30));
        Label accountCreated = new Label("Account created on: " + this.accountCreated.toString());
        Label gamesPlayed = new Label("Total games played: " + this.gamesPlayed);
        Label wins = new Label("Total wins: " + this.wins);
        Label losses = new Label("Total losses: " + this.losses);

        statsBox.getChildren().add(statistics);
        statsBox.getChildren().add(accountCreated);
        statsBox.getChildren().add(gamesPlayed);
        statsBox.getChildren().add(wins);
        statsBox.getChildren().add(losses);

        statsBox.setAlignment(Pos.TOP_CENTER);
        statsBox.setTranslateY(30);
        statsBox.setPadding(new Insets(10));
        statsBox.setSpacing(5);

        borderPane.setTop(toolBar);
        borderPane.setCenter(centerPane);
        borderPane.setRight(statsBox);

        Scene scene = new Scene(borderPane, 600, 300);

        primaryStage.setTitle("Account settings");
        primaryStage.setScene(scene);

        /*
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Width: " + newValue);
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Height: " + newValue);
        });

         */
    }

    public void clientGUI() {
        clientGUI.start();
    }
}
