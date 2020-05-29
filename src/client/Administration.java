package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Administration extends Application {

    private Stage stage;
    private GridPane gridPane;
    private BorderPane borderPane;
    private ToolBar toolBar;
    private TextField username;
    private TextField password;
    private VBox centerPane;
    private Socket socket;

    public static void main(String[] args) {
        launch(Administration.class);
    }

    private void connect() {
        try {
            socket = new Socket("localhost", 10000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        connect();
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

//        stage.setResizable(false);

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

    private void login() {
        credential();

        Text login = new Text("Login");
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            if (tryLogin()) {
                System.out.println(username.getText() + "|" + password.getText());
                clientGUI();
            }
        });

        gridPane.add(login, 0, 0);
        gridPane.add(loginButton, 1, 3);

        centerPane.getChildren().add(login);
        centerPane.getChildren().add(gridPane);
    }

    private boolean tryLogin() {
        if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF("LogU" + username.getText() + "|" + password.getText());
                String response = in.readUTF();
                if (response.equals("connected")) {
                    return true;
                } else if (response.contains("error")) {
                    if (response.charAt(5) == '2') {
                        //Invalid Password

                        System.out.println("invalid password");
                    } else if (response.charAt(5) == '3') {
                        //invalid Username
                        System.out.println("invalid name");

                    }else if (response.charAt(5) == '5'){
                        //User already connected
                        System.out.println("User already connected");
                    }
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

    private void register() {
        credential();

        Text register = new Text("Register");
        register.setFont(Font.font("Tahoma", FontWeight.NORMAL, 30));

        Button registerButton = new Button("Register account");
        registerButton.setOnAction(event -> {
            if (tryRegister()) {
                System.out.println(username.getText() + "|" + password.getText());
                clientGUI();
            }
        });

        gridPane.add(register, 0, 0);
        gridPane.add(registerButton, 1, 3);

        centerPane.getChildren().add(register);
        centerPane.getChildren().add(gridPane);
    }

    private boolean tryRegister() {
        if (!username.getText().isEmpty() && !password.getText().isEmpty()) {
            try {
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                out.writeUTF("RegU" + username.getText() + "|" + password.getText());
                String response = in.readUTF();
                if (response.equals("connected")) {
                    return true;
                } else if (response.contains("error")) {
                    //Name already in use
                    System.out.println("invalid name");
                }
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else return false;
    }

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

    protected void disconnect(){
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF("quit");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clientGUI() {
        ClientGUI gui = new ClientGUI(stage, this, socket);
        gui.start();
    }
}
