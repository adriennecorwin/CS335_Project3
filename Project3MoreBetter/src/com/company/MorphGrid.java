package com.company;

import javax.swing.*;
import java.awt.*;

public class MorphGrid extends JPanel {
    private int gridDim;
    private int panelSize;
    private int spacing;
    private int pointDragged[];
    private boolean isCopy=false;
    private ControlPoint correspondingPoint;
    private ControlPoint controlPoints[][];
    private Triangle triangles[][][];

    private void setUpControlPoints(){
        spacing = panelSize/gridDim;
        int x = spacing;
        int y = spacing;
        for(int i=0; i<this.gridDim-1; i++){
            for(int j=0; j<this.gridDim-1; j++){
                ControlPoint controlPoint = new ControlPoint(x, y);
                controlPoint.setRowCol(j, i);
                controlPoints[j][i]=controlPoint;
                x+=spacing;
            }
            y+=spacing;
            x=spacing;
        }
    }


    private void setUpTriangles(ControlPoint controlPoints[][]){
        for(int i=1; i<this.gridDim-1; i++) {
            for (int j=1; j<this.gridDim-1; j++) {
                triangles[j][i][0] = new Triangle(controlPoints[j-1][i-1], controlPoints[j-1][i], controlPoints[j][i]);
                triangles[j][i][1] = new Triangle(controlPoints[j-1][i-1], controlPoints[j][i-1], controlPoints[j][i]);
            }
        }
        int borderY=0;
        for(int i=0; i<this.gridDim; i++){
            if(i==0){
                triangles[0][i][0] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(0, borderY + spacing)), controlPoints[0][i]);
                triangles[0][i][1] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(spacing, borderY)), controlPoints[0][i]);
                triangles[this.gridDim-1][i][0] = new Triangle(new ControlPoint(new Point(panelSize-spacing, borderY)), controlPoints[9][i], new ControlPoint(new Point(panelSize, borderY+spacing)));
                triangles[this.gridDim-1][i][1] = new Triangle(new ControlPoint(new Point(panelSize-spacing, borderY)), new ControlPoint(new Point(panelSize, borderY)), new ControlPoint(new Point(panelSize, borderY+spacing)));
            }
            else if(i<10) {
                triangles[0][i][0] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(0, borderY + spacing)), controlPoints[0][i]);
                triangles[0][i][1] = new Triangle(new ControlPoint(new Point(0, borderY)), controlPoints[0][i-1], controlPoints[0][i]);
                triangles[this.gridDim-1][i][0] = new Triangle(controlPoints[9][i-1], controlPoints[9][i], new ControlPoint(new Point(panelSize, borderY+spacing)));
                triangles[this.gridDim-1][i][1] = new Triangle(controlPoints[9][i-1], new ControlPoint(new Point(panelSize, borderY)), new ControlPoint(new Point(panelSize, borderY+spacing)));
            }
            else{
                triangles[0][i][0] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(0, panelSize)), new ControlPoint(new Point(spacing, panelSize)));
                triangles[0][i][1] = new Triangle(new ControlPoint(new Point(0, borderY)), controlPoints[0][i-1], new ControlPoint(new Point(spacing, panelSize)));
                triangles[this.gridDim-1][i][0] = new Triangle(controlPoints[9][i-1], new ControlPoint(new Point(borderY, panelSize)), new ControlPoint(new Point(panelSize, panelSize)));
                triangles[this.gridDim-1][i][1] = new Triangle(controlPoints[9][i-1], new ControlPoint(new Point(panelSize, borderY)), new ControlPoint(new Point(panelSize, panelSize)));
            }
            borderY+=spacing;
        }

        int borderX=spacing;
        for(int i=1; i<this.gridDim-1; i++){
            triangles[i][0][0] = new Triangle(new ControlPoint(new Point(borderX, 0)), controlPoints[i-1][0], controlPoints[i][0]);
            triangles[i][0][1] = new Triangle(new ControlPoint(new Point(borderX, 0)), new ControlPoint(new Point(borderX+spacing, 0)), controlPoints[i][0]);
            triangles[i][this.gridDim-1][0] = new Triangle(controlPoints[i-1][9], new ControlPoint(new Point(borderX, panelSize)), new ControlPoint(new Point(borderX+spacing, panelSize)));
            triangles[i][this.gridDim-1][1] = new Triangle(controlPoints[i-1][9], controlPoints[i][9], new ControlPoint(new Point(borderX+spacing, panelSize)));
            borderX+=spacing;
        }
    }

    public MorphGrid(int gridDim){
        panelSize = 605;
        this.setPreferredSize(new Dimension(panelSize,panelSize));
        this.gridDim=gridDim+1;
        spacing = panelSize/this.gridDim;
        pointDragged = new int[2];

        setUpGrid();
    }

    MorphGrid(MorphGrid toCopy){
        this.setPreferredSize(new Dimension(605, 605));
        this.gridDim = toCopy.gridDim;
        this.panelSize = toCopy.panelSize;
        this.spacing = toCopy.spacing;
        this.correspondingPoint = toCopy.correspondingPoint;
        this.controlPoints = new ControlPoint[this.gridDim-1][this.gridDim-1];
        for(int i=0; i<this.gridDim-1; i++){
            for(int j=0; j<this.gridDim-1; j++){
                this.controlPoints[j][i]= new ControlPoint((int)toCopy.controlPoints[j][i].getX(), (int)toCopy.controlPoints[j][i].getY());
            }
        }
        this.triangles = new Triangle[this.gridDim][this.gridDim][2];
        for(int i=0; i<this.gridDim; i++){
            for(int j=0; j<this.gridDim; j++){
                for(int k=0; k<=1; k++){
                    this.triangles[j][i][k]=new Triangle(toCopy.triangles[j][i][k].getV1(), toCopy.triangles[j][i][k].getV2(), toCopy.triangles[j][i][k].getV3());
                }
            }
        }
        this.pointDragged = toCopy.pointDragged;
        isCopy=true;
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);
        for(int i=0; i<this.gridDim-1; i++){
            for(int j=0; j<this.gridDim-1; j++){
               // g.setColor(Color.BLACK);
                //Graphics2D g2 = (Graphics2D) g;
                //g2.setColor(Color.BLACK);
                if (pointDragged[0] == j && pointDragged[1] == i && !isCopy) {
                    g2.setColor(Color.RED);
                    g2.fill(controlPoints[j][i].getShape());
                    g2.setColor(Color.BLACK);
                }
                else if(controlPoints[j][i]==correspondingPoint && !isCopy){
                    g2.setColor(Color.RED);
                    g2.fill(controlPoints[j][i].getShape());
                    g2.setColor(Color.BLACK);
                }
                else {
                    g2.fill(controlPoints[j][i].getShape());
                }
            }
        }
        g.setColor(Color.BLACK);
        for(int i=0; i<this.gridDim; i++){
            for(int j=0; j<this.gridDim; j++){
                for(int k=0; k<=1; k++) {
                    g2.drawPolygon(triangles[j][i][k].getXPoints(), triangles[j][i][k].getYPoints(), 3);
                }
            }
        }
    }

    public void setUpGrid(){
        controlPoints = new ControlPoint[this.gridDim][this.gridDim];
        triangles = new Triangle[this.gridDim][this.gridDim][2];

        setUpControlPoints();
        setUpTriangles(controlPoints);

        repaint();
    }

    public void updateTriangles(int x, int y){
        controlPoints[pointDragged[0]][pointDragged[1]].setXY(x, y);

        for(int i=0; i<gridDim; i++) {
            for (int j = 0; j < gridDim; j++) {
                for (int k = 0; k <= 1; k++) {
                    if (triangles[j][i][k].controlledBy(pointDragged[0], pointDragged[1]) == 1) {
                        triangles[j][i][k].setControlPoint(1, controlPoints[pointDragged[0]][pointDragged[1]]);
                    }
                    if (triangles[j][i][k].controlledBy(pointDragged[0], pointDragged[1]) == 2) {
                        triangles[j][i][k].setControlPoint(2, controlPoints[pointDragged[0]][pointDragged[1]]);
                    }
                    if (triangles[j][i][k].controlledBy(pointDragged[0], pointDragged[1]) == 3) {
                        triangles[j][i][k].setControlPoint(3, controlPoints[pointDragged[0]][pointDragged[1]]);
                    }
                }
            }
        }

        repaint();
    }

    public void updateTrianglePreview(int row, int col, ControlPoint after){
        for(int i=0; i<gridDim; i++) {
            for (int j = 0; j < gridDim; j++) {
                for (int k = 0; k <= 1; k++) {
                    if (triangles[j][i][k].controlledBy(row, col) == 1) {
                        triangles[j][i][k].setControlPoint(1, after);
                    }
                    if (triangles[j][i][k].controlledBy(row, col) == 2) {
                        triangles[j][i][k].setControlPoint(2, after);
                    }
                    if (triangles[j][i][k].controlledBy(row, col) == 3) {
                        triangles[j][i][k].setControlPoint(3, after);
                    }
                }
            }
        }
        repaint();
    }


    public void setCorrespondingPoint(int[] correspondingPoint){
        this.correspondingPoint = controlPoints[correspondingPoint[0]][correspondingPoint[1]];
    }

    public void setPointDragged(int[] pointDragged){
        this.pointDragged = pointDragged;
    }

    public int getGridDim(){
        return gridDim;
    }

    public ControlPoint[][] getControlPoints(){
        return controlPoints;
    }

    public int getPanelSize(){
        return panelSize;
    }



}
