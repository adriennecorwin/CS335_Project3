package com.company;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class ControlPoint extends Point{
    private final int radius = 5; //radius of control point circle
    private Ellipse2D shape; //each control point will be a circle
    private int row; //row pos of control point in grid
    private int col; //col pos of control point

    //CONSTRUCTOR
    //initialize a control point as a point with specified x, y pos
    //and as being a circle in the panel to indicate the user can drag it
    public ControlPoint(int x, int y) {
        this.x = x;
        this.y = y;
        shape = new Ellipse2D.Float();
        shape.setFrame(this.x-radius, this.y-radius, radius*2, radius*2);
    }

    //ALTERNATIVE CONSTRUCTOR
    //intialize control point as a point with specified x, y pos
    //has no shape to indicate user cannot drag it
    //(for boundaries)
    public ControlPoint(Point point){
        this.x=point.x;
        this.y=point.y;

        row = -1; //these points will not be included in the control point array since they cannot change
        col= -1;
    }

    //GETTERS AND SETTERS
    public Ellipse2D getShape(){
        return shape;
    }

    public void setXY(int newX, int newY){
        this.x = newX;
        this.y = newY;
        shape.setFrame(x-radius, y-radius, radius*2, radius*2);
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
