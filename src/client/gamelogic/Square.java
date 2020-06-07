package client.gamelogic;

import org.jfree.fx.FXGraphics2D;

import java.awt.*;

public class Square {
    private Shape square;
    private Color color;

    public Square(Shape square, Color color) {
        this.square = square;
        this.color = color;
    }

    /**
     * Getter for the shape square.
     *
     * @return The shape Square.
     */
    public Shape getSquare() {
        return square;
    }

    /**
     * Setter for the shape Square.
     *
     * @param square Sets the shape Square.
     */
    public void setSquare(Shape square) {
        this.square = square;
    }

    /**
     * Setter for the color of the shape Square.
     *
     * @param color Sets the color of shape Square.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Draws the shape Square.
     *
     * @param graphics2D Context on which the shape Square is drawn.
     */
    public void drawFill(FXGraphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.fill(square);
    }
}
