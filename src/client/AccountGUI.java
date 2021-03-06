package client;

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

public class AccountGUI {

    private String username;
    private LocalDate accountCreated;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private Socket socket;

    private TextField oldPass;
    private TextField newPass;
    private TextField newPassConfirm;
    private LoginGUI loginGUI;
    private Stage primaryStage;

    private DataOutputStream out;
    private DataInputStream in;

    /**
     * Method to get the user data from the server.
     */
    private void getData() {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());

            out.writeUTF("GUsD");
            String response = in.readUTF();
            Scanner reader = new Scanner(response);
            reader.useDelimiter("/");

            username = reader.next();
            gamesPlayed = Integer.parseInt(reader.next());
            wins = Integer.parseInt(reader.next());
            losses = gamesPlayed - wins;
            accountCreated = LocalDate.parse(reader.next());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start method which sets the stage of the screen.
     *
     * @param primaryStage The class which is used to change the scene settings.
     * @param mainMenuGUI  Required for the back button. Calls upon the method start to change the scene.
     * @param loginGUI     Required for the sign out button. Calls upon the method start to change the scene.
     * @param socket       The class required to make connection to the server.
     */
    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, LoginGUI loginGUI, Socket socket) {
        this.primaryStage = primaryStage;
        this.loginGUI = loginGUI;
        this.socket = socket;

        getData();

        BorderPane borderPane = new BorderPane();

        //top
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> mainMenuGUI.start());

        ToolBar toolBar = new ToolBar();

        toolBar.getItems().add(backButton);

        borderPane.setTop(toolBar);

        //center
        GridPane credentials = new GridPane();
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
        changePassButton.setOnAction(e -> {
            try {
                out.writeUTF("ChPw" + oldPass.getText() + "|" + newPass.getText() + "|" + newPassConfirm.getText());
                String response = in.readUTF();
                if (response.equals("Conf")) {
                    oldPass.clear();
                    newPass.clear();
                    newPassConfirm.clear();
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        Text title = new Text(username + " Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTranslateX(10);

        VBox centerPane = new VBox();
        centerPane.getChildren().addAll(title, credentials);
        centerPane.setAlignment(Pos.CENTER_LEFT);

        borderPane.setCenter(centerPane);

        //right
        Label statistics = new Label("Stats");
        statistics.setFont(Font.font("Arial", FontPosture.ITALIC, 30));
        Label accountCreated = new Label("Account created on: " + this.accountCreated.toString());
        Label gamesPlayed = new Label("Total games played: " + this.gamesPlayed);
        Label wins = new Label("Total wins: " + this.wins);
        Label losses = new Label("Total losses: " + this.losses);
        Button buttonDelete = new Button("Delete account");
        buttonDelete.setOnAction(event -> deleteAccount());

        VBox statsBox = new VBox();

        statsBox.getChildren().addAll(statistics, accountCreated, gamesPlayed, wins, losses, buttonDelete);

        statsBox.setAlignment(Pos.TOP_CENTER);
        statsBox.setTranslateY(30);
        statsBox.setPadding(new Insets(10));
        statsBox.setSpacing(5);

        borderPane.setRight(statsBox);

        Scene scene = new Scene(borderPane, 600, 300);

        this.primaryStage.setTitle("Account settings");
        this.primaryStage.setScene(scene);
    }

    /**
     * Method that checks if it's possible to delete the account. Sends a message to the server asking to delete the
     * account. Responds with a confirmation that the account had been deleted.
     *
     * @param password A string that contains the password of the user.
     * @return A boolean with which the server has confirmed whether or not the account had been deleted.
     */
    private boolean tryDeleteAccount(String password) {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF("DelU" + password);
            String response = in.readUTF();
            return response.equals("Account deleted");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Method to create a popup in which the user is asked to put their username twice to delete the account with which
     * they logged in with.
     */
    private void deleteAccount() {
        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle("Delete account");

        BorderPane borderPaneDelete = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.close());

        toolBar.getItems().add(backButton);

        borderPaneDelete.setTop(toolBar);

        GridPane gridPane = new GridPane();

        gridPane.setPadding(new Insets(10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        PasswordField passwordField = new PasswordField();
        PasswordField passwordFieldConfirm = new PasswordField();

        gridPane.add(new Label("Password"), 0, 0);
        gridPane.add(passwordField, 1, 0);
        gridPane.add(new Label("Confirm password"), 0, 1);
        gridPane.add(passwordFieldConfirm, 1, 1);

        Button buttonConfirmDelete = new Button("Confirm delete account");
        buttonConfirmDelete.setOnAction(e -> {
            if (passwordFieldConfirm.getText().equals(passwordField.getText())) {
                if (tryDeleteAccount(passwordFieldConfirm.getText())) {
                    stage.close();
                    loginGUI.disconnect();
                    loginGUI.start(primaryStage);
                }
            }
            passwordField.clear();
            passwordFieldConfirm.clear();
        });

        gridPane.add(buttonConfirmDelete, 1, 2);

        borderPaneDelete.setCenter(gridPane);

        Scene scene = new Scene(borderPaneDelete);
        stage.setScene(scene);
        stage.show();
    }

}
