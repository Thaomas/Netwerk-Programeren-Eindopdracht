package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.Optional;

public class ConnectFour {

    private final int columns = 7;
    private final int rows = 6;
    private Color starter;

    private Disc[][] grid = new Disc[columns][rows];

    private Color turn;

    public ConnectFour() {
        starter = Color.red;
        turn = Color.red;
    }

    /**
     * Returns the color which starts the round
     * @return The abbreviation of the color red or yellow.
     */
    public String getStart() {
        if (starter.equals(Color.red)) {
            return "R";
        } else
            return "Y";
    }

    /**
     * Restarts the game and switches the color of the starting player.
     */
    public void restart() {
        if (starter.equals(Color.red)) {
            starter = Color.yellow;
            turn = Color.yellow;
            grid = new Disc[columns][rows];
        } else {
            starter = Color.red;
            turn = Color.red;
            grid = new Disc[columns][rows];
        }
    }

    /**
     * Method used to check if a disc object is present in that position in the grid
     * @param column The column of the grid.
     * @param row The row of the grid.
     * @return An optional object which can contain either a disc object or null/empty.
     */
    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= columns
                || row < 0 || row >= rows)
            return Optional.empty();

        return Optional.ofNullable(grid[column][row]);
    }

    /**
     * Method used create a disc object which will be placed in the grid on the client side.
     * @param column Which column the grid will be placed in.
     * @param color The color of the disc.
     * @return The created disc object which will be sent to both players in the game room lobby.
     */
    public Disc placeDisc(int column, Color color) {
        Disc disc = null;
        if (turn.equals(color)) {
            int row = rows - 1;
            while (row >= 0) {
                if (!getDisc(column, row).isPresent())
                    break;
                row--;
            }
            disc = new Disc(column, row, Color.red, 100);
            disc.setColor(color);
            grid[column][row] = disc;

            if (color.equals(Color.red))
                this.turn = Color.yellow;
            else
                this.turn = Color.red;
        }
        return disc;
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
