package client;

import com.sun.javafx.scene.control.skin.TableHeaderRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.SimplePropertyConverter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;

public class LeaderboardGUI {

    private Stage stage;
    private MainMenuGUI mainMenuGUI;
    private Socket socket;
    private HashMap<String, HashMap<String, Integer>> leaderboard;

    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket) {
        stage = primaryStage;
        this.mainMenuGUI = mainMenuGUI;
        this.socket = socket;
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF("GLea");
            leaderboard = new HashMap<>((HashMap<String, HashMap<String, Integer>>)new ObjectInputStream(socket.getInputStream()).readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> mainMenuGUI.start());

        toolBar.getItems().add(backButton);

        borderPane.setTop(toolBar);

        borderPane.setCenter(getDefaultView());

        Scene scene = new Scene(borderPane, 640, 480);
        stage.setTitle("Leaderboard");
        stage.setScene(scene);
        stage.show();
    }

    private TableView<SimplePropertyConverter> getDefaultView() {
        TableView<SimplePropertyConverter> tableView = new TableView<>();

        tableView.getStylesheets().add(this.getClass().getResource("/util/Focus.css").toExternalForm());

        TableColumn<SimplePropertyConverter, String> name = new TableColumn<>("Name");
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<SimplePropertyConverter, Integer> gamesPlayed = new TableColumn<>("Games played");
        gamesPlayed.setCellValueFactory(new PropertyValueFactory<>("gamesPlayed"));
        TableColumn<SimplePropertyConverter, String> gamesWon = new TableColumn<>("Wins");
        gamesWon.setCellValueFactory(new PropertyValueFactory<>("gamesWon"));
        TableColumn<SimplePropertyConverter, String> gamesLost = new TableColumn<>("Losses");
        gamesLost.setCellValueFactory(new PropertyValueFactory<>("gamesLost"));
        tableView.getColumns().addAll(name,gamesPlayed,gamesWon,gamesLost);

        name.prefWidthProperty().bind(tableView.widthProperty().divide(2));
        gamesPlayed.prefWidthProperty().bind(tableView.widthProperty().divide(4).subtract(42));
        gamesWon.prefWidthProperty().bind(tableView.widthProperty().divide(4).subtract(60));
        gamesLost.prefWidthProperty().bind(tableView.widthProperty().divide(4).subtract(60));

        SortedList<SimplePropertyConverter> sortedList = new SortedList<>(loadData());

        sortedList.comparatorProperty().bind(tableView.comparatorProperty());

        gamesWon.setSortType(TableColumn.SortType.DESCENDING);
        tableView.getSortOrder().addAll(gamesWon);
        tableView.setItems(sortedList);

        tableView.setEditable(false);

        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            final TableHeaderRow header = (TableHeaderRow) tableView.lookup("TableHeaderRow");
            header.reorderingProperty().addListener((o, oldVal, newVal) -> header.setReordering(false));
        });

        return tableView;
    }

    private ObservableList<SimplePropertyConverter> loadData(){
        ObservableList<SimplePropertyConverter> data = FXCollections.observableArrayList();

        for (String key : leaderboard.keySet()){
            data.add(new SimplePropertyConverter(key, leaderboard.get(key).get("played"), leaderboard.get(key).get("won")));
        }

        return data;
    }
}