package client.gamelogic;


import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.Serializable;

public class Disc implements Serializable {

    private final Shape circle;
    private Color color;

    public Disc(int column, int row, Color color, double size) {
        this.circle = new Ellipse2D.Double(column * 110 + 20, row * 110 + 20, size, size);
        this.color = color;
    }

    /**
     * Draws the created circle with the given color.
     *
     * @param graphics2D The context on which the circle is drawn on.
     */
    public void draw(FXGraphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.fill(circle);
        graphics2D.setColor(Color.black);
        graphics2D.draw(circle);
    }

    /**
     * Getter for the shape Circle.
     *
     * @return the shape Circle.
     */
    public Shape getCircle() {
        return circle;
    }

    /**
     * Setter for the color of the cicle.
     *
     * @param color Sets the color.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Getter for the color of the circle.
     *
     * @return The color of the circle.
     */
    public Color getColor() {
        return color;
    }
}
