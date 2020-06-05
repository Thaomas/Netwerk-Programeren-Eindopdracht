package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.Optional;

public class ConnectFour {

    private final int SQUARE_SIZE = 100;
    private final int COLUMNS = 7;
    private final int ROWS = 6;
    private Color starter;

    private Disc[][] grid = new Disc[COLUMNS][ROWS];

    private Color turn;

    public ConnectFour() {
        starter = Color.red;
        turn = Color.red;
    }

    public Disc placeDisc(int column, Color color) {
        Disc disc = null;
        if (turn.equals(color)) {

            int row = ROWS - 1;

            while (row >= 0) {
                if (!getDisc(column, row).isPresent())
                    break;

                row--;
            }

            disc = new Disc(new java.awt.geom.Point2D.Double(
                    column * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5,
                    row * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5), Color.red, SQUARE_SIZE);


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
        if (column < 0 || column >= COLUMNS
                || row < 0 || row >= ROWS)
            return Optional.empty();

        return Optional.ofNullable(grid[column][row]);
    }

    public boolean checkWin(Color color) {

        for (int i = 0; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS - 3; j++) {
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

        for (int i = 0; i < COLUMNS - 3; i++) {
            for (int j = 0; j < ROWS; j++) {
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

        for (int i = 3; i < COLUMNS; i++) {
            for (int j = 0; j < ROWS - 3; j++) {
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

        for (int i = 3; i < COLUMNS; i++) {
            for (int j = 3; j < ROWS; j++) {
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
