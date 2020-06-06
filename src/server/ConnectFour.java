package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.Optional;

public class ConnectFour {
    
    //todo if game ends stop adding
    //todo if game ends swap color if game ends
    //todo game end screen
    //todo replay vote
    //todo join game refreshing

    private final int SQUARE_SIZE = 100;
    private final int columns = 7;
    private final int rows = 6;
    private Color starter;

    private Disc[][] grid = new Disc[columns][rows];

    private Color turn;

    public ConnectFour() {
        starter = Color.red;
        turn = Color.red;
    }
    
    public void restart(){
        if (starter.equals(Color.red)){
            starter = Color.yellow;
            turn = Color.yellow;
            grid = new Disc[columns][rows];
        }else {
            starter = Color.red;
            turn = Color.red;
            grid = new Disc[columns][rows];
        }
    }

    public Disc placeDisc(int column, Color color) {
        Disc disc = null;
        if (turn.equals(color)) {

            int row = rows - 1;

        while (row >= 0) {
            if (!getDisc(column, row).isPresent())
                break;

            row--;
        }

            disc = new Disc(column,row, Color.red, SQUARE_SIZE);


            disc.setColor(color);

            grid[column][row] = disc;

            if (color.equals(Color.red))
                this.turn=Color.yellow;
            else
                this.turn=Color.red;
        }
        return disc;
    }

    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= columns
                || row < 0 || row >= rows)
            return Optional.empty();

        return Optional.ofNullable(grid[column][row]);
    }

    public boolean checkWin(Color color) {

        for (int i = 0; i < columns; i++) {
            for (int j = 0; j < rows - 3; j++) {
                // Vertical Check
                if (grid[i][j] != null && grid[i][j + 1] != null && grid[i][j + 2] != null && grid[i][j + 3] != null &&
                        grid[i][j].getColor().equals(color) &&
                        grid[i][j + 1].getColor().equals(color) &&
                        grid[i][j + 2].getColor().equals(color) &&
                        grid[i][j + 3].getColor().equals(color)) {
                    return true;
                }
            }
        }

        for (int i = 0; i < columns - 3; i++) {
            for (int j = 0; j < rows; j++) {
                // Horizontal Check
                if (grid[i][j] != null && grid[i + 1][j] != null && grid[i + 2][j] != null && grid[i + 3][j] != null &&
                        grid[i][j].getColor().equals(color) &&
                        grid[i + 1][j].getColor().equals(color) &&
                        grid[i + 2][j].getColor().equals(color) &&
                        grid[i + 3][j].getColor().equals(color)) {
                    return true;
                }
            }
        }

        for (int i = 3; i < columns; i++) {
            for (int j = 0; j < rows - 3; j++) {
                // Ascending DiagonalCheck
                if (grid[i][j] != null && grid[i - 1][j + 1] != null && grid[i - 2][j + 2] != null && grid[i - 3][j + 3] != null &&
                        grid[i][j].getColor().equals(color) &&
                        grid[i - 1][j + 1].getColor().equals(color) &&
                        grid[i - 2][j + 2].getColor().equals(color) &&
                        grid[i - 3][j + 3].getColor().equals(color)) {
                    return true;
                }
            }
        }

        for (int i = 3; i < columns; i++) {
            for (int j = 3; j < rows; j++) {
                // Descending DiagonalCheck
                if (grid[i][j] != null && grid[i - 1][j - 1] != null && grid[i - 2][j - 2] != null && grid[i - 3][j - 3] != null &&
                        grid[i][j].getColor().equals(color) &&
                        grid[i - 1][j - 1].getColor().equals(color) &&
                        grid[i - 2][j - 2].getColor().equals(color) &&
                        grid[i - 3][j - 3].getColor().equals(color)) {
                    return true;
                }
            }
        }

        return false;
    }
}
