package util;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SimplePropertyConverter {

    private final SimpleStringProperty name;
    private final SimpleIntegerProperty gamesPlayed;
    private final SimpleIntegerProperty gamesWon;
    private final SimpleIntegerProperty gamesLost;

    public SimplePropertyConverter(String name, int gamesPlayed, int gamesWon) {
        this.name = new SimpleStringProperty(name);
        this.gamesPlayed = new SimpleIntegerProperty(gamesPlayed);
        this.gamesWon = new SimpleIntegerProperty(gamesWon);
        this.gamesLost = new SimpleIntegerProperty(gamesPlayed - gamesWon);
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public int getGamesPlayed() {
        return gamesPlayed.get();
    }

    public SimpleIntegerProperty gamesPlayedProperty() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon.get();
    }

    public SimpleIntegerProperty gamesWonProperty() {
        return gamesWon;
    }

    public int getGamesLost() {
        return gamesLost.get();
    }

    public SimpleIntegerProperty gamesLostProperty() {
        return gamesLost;
    }
}
