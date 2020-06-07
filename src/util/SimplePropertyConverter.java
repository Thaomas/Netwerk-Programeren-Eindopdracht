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

    /**
     * Getter for the nameProperty.
     * @return SimpleStringProperty for the column name.
     */
    public SimpleStringProperty nameProperty() {
        return name;
    }

    /**
     * Getter for the gamesPlayedProperty.
     * @return SimpleStringProperty for the column gamesPlayed.
     */
    public SimpleIntegerProperty gamesPlayedProperty() {
        return gamesPlayed;
    }

    /**
     * Getter for the gamesWonProperty.
     * @return SimpleStringProperty for the column gamesWon.
     */
    public SimpleIntegerProperty gamesWonProperty() {
        return gamesWon;
    }

    /**
     * Getter for the gamesLostProperty.
     * @return SimpleStringProperty for the column gamesLost.
     */
    public SimpleIntegerProperty gamesLostProperty() {
        return gamesLost;
    }
}
