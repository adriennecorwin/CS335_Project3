package com.company;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ControlPoint extends Point{
    private final int radius = 5;
    private Ellipse2D shape;
    private int row;
    private int col;

    public ControlPoint(int x, int y) {
        this.x = x;
        this.y = y;
        shape = new Ellipse2D.Float();
        shape.setFrame(this.x-radius, this.y-radius, radius*2, radius*2);
    }

    public ControlPoint(Point point){
        this.x=point.x;
        this.y=point.y;

        row = -1;
        col= -1;
    }

    public Ellipse2D getShape(){
        return shape;
    }

    public void setXY(int newX, int newY){
        this.x = newX;
        this.y = newY;
        shape.setFrame(x-radius, y-radius, radius*2, radius*2);
    }

    public int getRadius() {
        return radius;
    }

    public void setRowCol(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

}
