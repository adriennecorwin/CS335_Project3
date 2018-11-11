package com.company;


import java.awt.*;

public class Triangle {
    private Point v1;
    private Point v2;
    private Point v3;

    public Triangle(Point v1, Point v2, Point v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    public int[] getXPoints() {
        int xPoints[] = {(int) v1.getX(), (int) v2.getX(), (int) v3.getX()};
        return xPoints;
    }

    public int[] getYPoints() {
        int yPoints[] = {(int) v1.getY(), (int) v2.getY(), (int) v3.getY()};
        return yPoints;
    }


    public int controlledBy(ControlPoint controlPoint) {
        if (v1 == controlPoint) {
            return 1;
        } else if (v2 == controlPoint) {
            return 2;
        } else if (v3 == controlPoint) {
            return 3;
        } else {
            return 0;
        }
    }

    public void setControlPoint(int v, ControlPoint controlPoint) {
        if (v == 1) {
            v1 = controlPoint;
        } else if (v == 2) {
            v2 = controlPoint;
        } else if (v == 3) {
            v3 = controlPoint;
        }
    }
}