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

import java.time.LocalDate;
import java.util.Date;

public class Account {

    private Stage stage;

    private String username;
    private String password;
    private LocalDate accountCreated;
    private int gamesPlayed;
    private int wins;
    private int losses;

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

    public void start(Stage primaryStage, ClientGUI clientGUI) {

        testData();

        stage = primaryStage;
        this.clientGUI = clientGUI;
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

        credentials.add(new Label("Old password: "),0,0);
        oldPass = new PasswordField();
        credentials.add(oldPass,1,0);

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
        });
        HBox visiblePassword = new HBox(viewBox, visible);
        credentials.add(visiblePassword, 2, 0);

        credentials.add(new Label("New password: "),0,1);
        newPass = new PasswordField();
        credentials.add(newPass,1,1);

        credentials.add(new Label("New password \n confirmation: "),0,2);
        newPassConfirm = new PasswordField();
        credentials.add(newPassConfirm,1,2);

        Button changePassButton = new Button("Change password");
        credentials.add(changePassButton,1,3);

        Text title = new Text(username + " Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTranslateX(10);
        centerPane.getChildren().add(title);
        centerPane.getChildren().add(credentials);
        centerPane.setAlignment(Pos.CENTER_LEFT);

        Label statistics = new Label("Stats");
        statistics.setFont(Font.font("Arial", FontPosture.ITALIC,30));
        Label accountCreated = new Label("Account created on: " + this.accountCreated.toString());
        Label gamesPlayed = new Label("Total games played: " + String.valueOf(this.gamesPlayed));
        Label wins = new Label("Total wins: " + String.valueOf(this.wins));
        Label losses = new Label("Total losses: " + String.valueOf(this.losses));

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

        Scene scene = new Scene(borderPane,600,300);

        stage.setTitle("Account settings");
        stage.setScene(scene);

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
