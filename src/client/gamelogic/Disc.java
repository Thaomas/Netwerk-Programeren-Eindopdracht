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

    public void draw(FXGraphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.fill(circle);
        graphics2D.setColor(Color.black);
        graphics2D.draw(circle);
    }

    public Shape getCircle() {
        return circle;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }
}
