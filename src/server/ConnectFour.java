package server;

import client.gamelogic.Disc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Optional;

public class ConnectFour {

    private final int SQUARE_SIZE = 100;
    private final int COLUMNS = 7;
    private final int ROWS = 6;

    private Disc[][] grid = new Disc[COLUMNS][ROWS];

    private boolean redMove = true;

    public Disc placeDisc(int column) {
        int row = ROWS - 1;

        while (row >= 0) {
            if (!getDisc(column, row).isPresent())
                break;

            System.out.println("test row:" + row);
            row--;
        }

        Disc disc = new Disc(new java.awt.geom.Point2D.Double(
                column * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5,
                row * (SQUARE_SIZE + 10) + SQUARE_SIZE / 5), Color.red, SQUARE_SIZE);

        if (redMove) {
            disc.setColor(Color.red);
        } else {
            disc.setColor(Color.yellow);
        }

        redMove = !redMove;
        grid[column][row] = disc;
//        discs.add(disc);

        if(ConnectFour.checkWin(grid,Color.red,COLUMNS,ROWS)){
            System.out.println("GAME OVER - RED WINS");
//            mouseDisabled = false;
        }
        if(ConnectFour.checkWin(grid,Color.yellow,COLUMNS,ROWS)){
            System.out.println("GAME OVER - YELLOW WINS");
//            mouseDisabled = false;
        }

        System.out.println("Disc column: " + column + ", row: " + row);

        return disc;
    }

    private Optional<Disc> getDisc(int column, int row) {
        if (column < 0 || column >= COLUMNS
                || row < 0 || row >= ROWS)
            return Optional.empty();

        return Optional.ofNullable(grid[column][row]);
    }

    public static boolean checkWin(Disc[][] grid, Color color, int COLUMNS, int ROWS) {

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
