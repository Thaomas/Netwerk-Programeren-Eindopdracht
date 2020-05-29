package client.gamelogic;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import org.jfree.fx.FXGraphics2D;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.geom.Point2D;

public class Square {
    private Shape square;
    private Point2D position;
    private Color color;
    double size;

    public Square(Shape square, Color color) {
        this.square = square;
        this.position = new Point2D.Double(0,0);
        this.color = color;
    }

    public Shape getSquare() {
        return square;
    }

    public void setSquare(Shape square) {
        this.square = square;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void draw(FXGraphics2D graphics2D){
        graphics2D.translate(position.getX(),position.getY());
        graphics2D.setColor(color);
        graphics2D.draw(square);
    }

    public void drawFill(FXGraphics2D graphics2D){
        graphics2D.translate(position.getX(),position.getY());
        graphics2D.setColor(color);
        graphics2D.fill(square);
    }
}
