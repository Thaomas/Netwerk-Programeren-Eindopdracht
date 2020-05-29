package client.gamelogic;


import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Disc extends Node {

    private Shape circle;
    private Point2D position;
    private Color color;
    double size;

    public Disc(Point2D position, Color color, double size) {
        this.circle = new Ellipse2D.Double(position.getX(), position.getY(), size,size);
        this.position = position;
        this.color = color;
        this.size = size;
    }

    public Point2D getPosition() {
        return position;
    }

    public void setPosition(Point2D position) {
        this.position = position;
    }

    public void draw(FXGraphics2D graphics2D){
//        AffineTransform affineTransform = new AffineTransform();
//        graphics2D.translate(position.getX(),position.getY());
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

    protected NGNode impl_createPeer() {
        return null;
    }

    public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        return null;
    }

    protected boolean impl_computeContains(double localX, double localY) {
        return false;
    }

    public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx) {
        return null;
    }
}
