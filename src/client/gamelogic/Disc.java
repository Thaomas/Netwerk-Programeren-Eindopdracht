package client.gamelogic;


import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Disc implements Serializable {

    private Shape circle;
    private Point2D position;
    private Color color;
    double size;

    public Disc(Point2D position, Color color, double size) {
        this.circle = new Ellipse2D.Double(position.getX(), position.getY(), size, size);
        this.position = position;
        this.color = color;
        this.size = size;
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
