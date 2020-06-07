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

    public Shape getSquare() {
        return square;
    }

    public void setSquare(Shape square) {
        this.square = square;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void drawFill(FXGraphics2D graphics2D) {
        graphics2D.setColor(color);
        graphics2D.fill(square);
    }
}
