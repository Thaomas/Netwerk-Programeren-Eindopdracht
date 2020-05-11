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

public class Account {

    private Stage stage;

    private String username = "'Username'";
    private String password;
    private LocalDate accountCreated;
    private int gamesPlayed;
    private int wins;
    private int losses;

    private TextField oldPass;
    private TextField newPass;
    private TextField newPassConfirm;

    public void start(Stage primaryStage) {
        stage = primaryStage;
        BorderPane borderPane = new BorderPane();
        ToolBar toolBar = new ToolBar();
        GridPane credentials = new GridPane();
        VBox centerPane = new VBox();
        VBox statsBox = new VBox();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            clientGUI();
        });

        toolBar.getItems().add(backButton);

        credentials.setVgap(10);
        credentials.setHgap(10);
        credentials.setPadding(new Insets(10));

        credentials.add(new Label("Old password: "),0,0);
        oldPass = new PasswordField();
        credentials.add(oldPass,1,0);

        credentials.add(new Label("New password: "),0,1);
        newPass = new PasswordField();
        credentials.add(newPass,1,1);

        credentials.add(new Label("New password \n confirmation: "),0,2);
        newPassConfirm = new PasswordField();
        credentials.add(newPassConfirm,1,2);

        Label visible = new Label("View password?");
        CheckBox viewBox = new CheckBox();
        viewBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String text = oldPass.getText();
            if (viewBox.isSelected()) {
                oldPass = new TextField();
            } else {
                oldPass = new PasswordField();
            }
            oldPass.setText(text);
            credentials.add(oldPass, 1, 0);
        });
        HBox visiblePassword = new HBox(viewBox, visible);
        credentials.add(visiblePassword, 2, 0);

        Text title = new Text(username + " Settings");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        centerPane.getChildren().add(title);
        centerPane.getChildren().add(credentials);
        centerPane.setAlignment(Pos.CENTER);

        Label statistics = new Label("Stats");
        statistics.setFont(Font.font("Arial", FontPosture.ITALIC,40));
        Label gamesPlayed = new Label("Total games played: " + String.valueOf(this.gamesPlayed));
        Label wins = new Label("Total wins: " + String.valueOf(this.wins));
        Label losses = new Label("Total losses: " + String.valueOf(this.losses));

        statsBox.getChildren().add(statistics);
        statsBox.getChildren().add(gamesPlayed);
        statsBox.getChildren().add(wins);
        statsBox.getChildren().add(losses);

        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setPadding(new Insets(10));

        borderPane.setTop(toolBar);
        borderPane.setCenter(centerPane);
        borderPane.setRight(statsBox);

        Scene scene = new Scene(borderPane,600,600);

        stage.setTitle("Account settings");
        stage.setScene(scene);
    }

    public void clientGUI() {
        ClientGUI gui = new ClientGUI();
        gui.start(stage);
    }
}
