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

    private HashMap<String, HashMap<String, Integer>> leaderboard;

    /**
     * Start method which sets the scene.
     *
     * @param primaryStage The class which is used to change the scene settings.
     * @param mainMenuGUI  Required for the back button. Calls upon the method start to change the scene.
     * @param socket       The class required to make connection to the server.
     */
    public void start(Stage primaryStage, MainMenuGUI mainMenuGUI, Socket socket) {
        try {
            new DataOutputStream(socket.getOutputStream()).writeUTF("GLea");
            leaderboard = new HashMap<>((HashMap<String, HashMap<String, Integer>>) new ObjectInputStream(socket.getInputStream()).readObject());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        BorderPane borderPane = new BorderPane();

        ToolBar toolBar = new ToolBar();

        Button backButton = new Button("Back");
        backButton.setOnAction(event -> mainMenuGUI.start());

        toolBar.getItems().add(backButton);

        borderPane.setTop(toolBar);

        borderPane.setCenter(getTable());

        Scene scene = new Scene(borderPane, 640, 480);
        primaryStage.setTitle("Leaderboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Method used to set the table with data of all the registerd players on the basis of their their total games
     * played, won and lost.
     *
     * @return TableView with data of each individual player and their achievements.
     */
    private TableView<SimplePropertyConverter> getTable() {
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
        tableView.getColumns().addAll(name, gamesPlayed, gamesWon, gamesLost);

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

    /**
     * Loads the data from the server into the TableView.
     *
     * @return List full of data.
     */
    private ObservableList<SimplePropertyConverter> loadData() {
        ObservableList<SimplePropertyConverter> data = FXCollections.observableArrayList();

        for (String key : leaderboard.keySet()) {
            data.add(new SimplePropertyConverter(key, leaderboard.get(key).get("played"), leaderboard.get(key).get("won")));
        }

        return data;
    }
}