package com.company;

public class Triangle {
    private ControlPoint v1; //vertex 1
    private ControlPoint v2; //vertex 2
    private ControlPoint v3; //vertex 3


    //CONSTRUCTOR
    //initialize with 3 vertices
    public Triangle(ControlPoint v1, ControlPoint v2, ControlPoint v3) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
    }

    //return all x pos of each vertex
    public int[] getXPoints() {
        int xPoints[] = {(int) v1.getX(), (int) v2.getX(), (int) v3.getX()};
        return xPoints;
    }

    //return all y pos of each vertex
    public int[] getYPoints() {
        int yPoints[] = {(int) v1.getY(), (int) v2.getY(), (int) v3.getY()};
        return yPoints;
    }


    //return which vertex of triangle is controlled by control point and pos row, col in control point array
    public int controlledBy(int row, int col) {

        if (v1.getRow()==row && v1.getCol()==col) {
            return 1;
        } else if (v2.getRow()==row && v2.getCol()==col) {
            return 2;
        } else if (v3.getRow()==row && v3.getCol()==col) {
            return 3;
        } else {
            return 0;
        }
    }

    //sets the vth vertex of triangle to be the specified control point
    public void setControlPoint(int v, ControlPoint controlPoint) {
        if (v == 1) {
            v1 = controlPoint;
        } else if (v == 2) {
            v2 = controlPoint;
        } else if (v == 3) {
            v3 = controlPoint;
        }
    }

    public ControlPoint getV1() {
        return v1;
    }

    public ControlPoint getV2() {
        return v2;
    }

    public ControlPoint getV3() {
        return v3;
    }

    public ControlPoint getV(int i){
        if(i==1){
            return v1;
        }
        else if(i==2){
            return v2;
        }
        else if(i==3){
            return v3;
        }
        else{
            return null;
        }
    }


}
