package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import util.AlertHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class LoginGUI extends Application {

    private Stage stage;
    private GridPane gridPane;
    private BorderPane borderPane;
    private ToolBar toolBar;
    private TextField username;
    private TextField password;
    private VBox centerPane;
    private Socket socket;
    private boolean isConnected = false;

    /**
     * Method used to connect to the server.
     */
    private void connect() {
        if (!isConnected) {
            try {
                socket = new Socket("localhost", 10000);
                isConnected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to start the first stage of the application.
     *
     * @param primaryStage The class used to start the scene.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

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

        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Sets the initial settings of the scene.
     */
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

        stage.setTitle("Connect 4");
    }

    /**
     * Login scene which asks only for the username and password to login to an existing account on the server.
     */
    private void login() {
        credential();

        Text login = new Text("Login");
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            if (tryLogin()) {
                new MainMenuGUI(stage, this, socket).start();
            }
        });

        username.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                password.requestFocus();
            }
        });

        password.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                loginButton.fire();
            }
        });

        gridPane.add(login, 0, 0);
        gridPane.add(loginButton, 1, 3);

        centerPane.getChildren().add(login);
        centerPane.getChildren().add(gridPane);

        stage.setTitle(login.getText());
    }

    /**
     * A check which sends a message to the server to see if its possible to login.
     *
     * @return Whether the login is successful or not.
     */
    private boolean tryLogin() {
        connect();
        if (isConnected && !username.getText().isEmpty() && !password.getText().isEmpty()) {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF("LogU" + username.getText() + "|" + password.getText());
                String response = in.readUTF();
                if (response.equals("connected")) {
                    return true;
                } else if (response.contains("error")) {
                    switch (response.charAt(5)) {
                        case '2':
                            AlertHandler.show(Alert.AlertType.ERROR, "Invalid Password", "Invalid Password", "Entered password is incorrect.");
                            break;
                        case '3':
                            AlertHandler.show(Alert.AlertType.ERROR, "Invalid Name", "Invalid Name", "No user with entered name exists.");
                            break;
                        case '5':
                            AlertHandler.show(Alert.AlertType.ERROR, "User already connected", "User already connected", "User is already connected.");
                            break;

                    }
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

    /**
     * Register which asks for the username and password of the password to create an account.
     */
    private void register() {
        credential();

        Text register = new Text("Register");
        register.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        Button registerButton = new Button("Register account");
        registerButton.setOnAction(event -> {
            if (tryRegister()) {
                new MainMenuGUI(stage, this, socket).start();
            }
        });

        username.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                password.requestFocus();
            }
        });

        password.setOnKeyPressed(e -> {
            // On Enter press
            if (e.getCode() == KeyCode.ENTER) {
                registerButton.fire();
            }
        });

        gridPane.add(register, 0, 0);
        gridPane.add(registerButton, 1, 3);

        centerPane.getChildren().add(register);
        centerPane.getChildren().add(gridPane);

        stage.setTitle(register.getText());
    }

    /**
     * Sends a message to the server to check if its possible to create an account with the given credentials.
     *
     * @return Whether the creation of the account successful was or not.
     */
    private boolean tryRegister() {
        connect();
        if (isConnected && !username.getText().isEmpty() && !password.getText().isEmpty()) {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF("RegU" + username.getText() + "|" + password.getText());
                String response = in.readUTF();
                if (response.equals("connected")) {
                    return true;
                } else if (response.contains("error")) {
                    //Name already in use
                    AlertHandler.show(Alert.AlertType.ERROR, "Invalid Name", "Invalid Name", "Username is already taken.");
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

    /**
     * A method used in both login and register where part of the scene such as the button and fields where the
     * user credentials are typed into are initialized and added to the scene.
     */
    private void credential() {
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

    /**
     * Method used to logout and disconnect the user that is connected to the server.
     */
    public void disconnect() {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("quit");
            socket.close();
            isConnected = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
