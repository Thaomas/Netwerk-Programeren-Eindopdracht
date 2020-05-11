package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Administration extends Application {

    private Stage stage;
    private GridPane gridPane;
    private BorderPane borderPane;
    private ToolBar toolBar;
    private TextField username;
    private TextField password;
    private VBox centerPane;

    @Override
    public void start(Stage primaryStage) {
        borderPane = new BorderPane();
        gridPane = new GridPane();
        centerPane = new VBox(10);

        centerPane.setAlignment(Pos.CENTER);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> startScene());

        toolBar = new ToolBar();
        toolBar.getItems().add(backButton);

        borderPane.setCenter(centerPane);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);

        startScene();

        Scene scene = new Scene(borderPane, 400, 300);

        stage = primaryStage;
        stage.setResizable(false);
        stage.setTitle("Connect 4 login");
        stage.setScene(scene);
        stage.show();

        /*
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Width: " + newValue);
        });

        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("Height: " + newValue);
        });

         */
    }

    public void startScene() {
        borderPane.setTop(null);
        gridPane.getChildren().clear();
        centerPane.getChildren().clear();

        Text signIn = new Text("Sign In");
        signIn.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        Button loginButton = new Button("Login to existing account");

        loginButton.setOnAction(e -> login());

        Button registerButton = new Button("Register new account");

        registerButton.setOnAction(e -> register());

        gridPane.add(loginButton, 0, 1);
        gridPane.add(registerButton, 1, 1);

        centerPane.getChildren().add(signIn);
        centerPane.getChildren().add(gridPane);
    }

    public void login() {
        credential();

        Text login = new Text("Login");
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            System.out.println(username.getText() + "|" + password.getText());

            clientGUI();
        });

        gridPane.add(login, 0, 0);
        gridPane.add(loginButton, 1, 3);

        centerPane.getChildren().add(login);
        centerPane.getChildren().add(gridPane);
    }

    public void register() {
        credential();

        Text register = new Text("Register");
        register.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        Button registerButton = new Button("Register account");
        registerButton.setOnAction(event -> {
            System.out.println(username.getText() + "|" + password.getText());

            login();
        });

        gridPane.add(register, 0, 0);
        gridPane.add(registerButton, 1, 3);

        centerPane.getChildren().add(register);
        centerPane.getChildren().add(gridPane);
    }

    public void credential() {
        borderPane.setTop(toolBar);
        gridPane.getChildren().clear();
        centerPane.getChildren().clear();

        gridPane.add(new Label("Username"), 0, 1);
        username = new TextField();
        gridPane.add(username, 1, 1);

        gridPane.add(new Label("Password"), 0, 2);
        password = new PasswordField();
        gridPane.add(password, 1, 2);

        Label visible = new Label("View password?");
        CheckBox viewBox = new CheckBox();
        viewBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            String text = password.getText();
            if (viewBox.isSelected()) {
                password = new TextField();
            } else {
                password = new PasswordField();
            }
            password.setText(text);
            gridPane.add(password, 1, 2);
        });
        HBox visiblePassword = new HBox(viewBox, visible);
        gridPane.add(visiblePassword, 3, 2);
    }


    public void clientGUI() {
        ClientGUI gui = new ClientGUI();
        gui.start(stage);
    }

    public static void main(String[] args) {
//        Client client = new Client();
        launch(Administration.class);
//        client.connect();
    }
}
