package com.company;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ControlPoint extends Point{
    private final int radius = 10;
    private Ellipse2D shape;

    public ControlPoint(int x, int y) {
        this.x = x;
        this.y = y;
        shape = new Ellipse2D.Float();
        shape.setFrame(this.x, this.y, radius, radius);
    }

    public Ellipse2D getShape(){
        return shape;
    }

    public void setXY(int newX, int newY){
        x = newX;
        y = newY;
        shape.setFrame(x, y, radius, radius);
    }
}