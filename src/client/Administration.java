package client;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Administration extends Application {

    private Stage stage;
    private GridPane gridPane;
    private BorderPane borderPane;
    private ToolBar toolBar;

    @Override
    public void start(Stage primaryStage) throws Exception {
        borderPane = new BorderPane();
        gridPane = new GridPane();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> {
            startScene();
        });

        toolBar = new ToolBar();
        toolBar.getItems().add(backButton);

        borderPane.setCenter(gridPane);

        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));
        gridPane.setAlignment(Pos.CENTER);

        startScene();

        Scene scene = new Scene(borderPane,360,300);

        stage = primaryStage;
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

    public void startScene(){
        borderPane.setTop(null);
        gridPane.getChildren().clear();

        Text signIn = new Text("Sign In");
        signIn.setFont(Font.font("Arial",FontWeight.BOLD,30));

        Button loginButton = new Button("Login to existing account");

        loginButton.setOnAction(event -> {
            login();
        });

        Button registerButton = new Button("Register new account");

        registerButton.setOnAction(event -> {
            register();
        });

        gridPane.add(signIn,0,0);
        gridPane.add(loginButton,0,1);
        gridPane.add(registerButton,1,1);
    }

    public void login(){
        borderPane.setTop(toolBar);
        gridPane.getChildren().clear();

        Text login = new Text("Login");
        login.setFont(Font.font("Tahoma", FontWeight.NORMAL,30));
        gridPane.add(login,0,0);

        credential();

        Button loginButton = new Button("Login");
        loginButton.setOnAction(event -> {
            clientGUI();
        });
        gridPane.add(loginButton,1,3);
    }

    public void register(){
        borderPane.setTop(toolBar);
        gridPane.getChildren().clear();

        Text register = new Text("Register");
        register.setFont(Font.font("Tahoma", FontWeight.NORMAL,30));
        gridPane.add(register,0,0);

        credential();

        Button registerButton = new Button("Register account");
        gridPane.add(registerButton,1,3);
    }

    public void credential(){
        gridPane.add(new Label("Username"),0,1);
        TextField username = new TextField();
        gridPane.add(username,1,1);

        gridPane.add(new Label("Password"),0,2);
        PasswordField password = new PasswordField();
        gridPane.add(password,1,2);
    }


    public void clientGUI(){
        ClientGUI gui = new ClientGUI(stage);
        gui.start();
    }

    public static void main(String[] args) {
//        Client client = new Client();
        launch(Administration.class);
//        client.connect();
    }
}
