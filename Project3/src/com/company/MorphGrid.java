package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MorphGrid extends JPanel {
    private int gridDim;
    private int panelSize;
    private int spacing;
    private boolean isDragging = false;
    private int pointDragged[];
    private ControlPoint controlPoints[][];
    private Triangle triangles[][][];

    public MorphGrid(int gridDim){
        this.setPreferredSize(new Dimension(600,600));
        this.setBorder(BorderFactory.createLineBorder(Color.black));
        panelSize = 600;
        this.gridDim=gridDim+1;
        spacing = panelSize/this.gridDim;
        controlPoints = new ControlPoint[this.gridDim][this.gridDim];
        triangles = new Triangle[this.gridDim][this.gridDim][2];
        int x = spacing;
        int y = spacing;
        for(int i=0; i<this.gridDim-1; i++){
            for(int j=0; j<this.gridDim-1; j++){
                ControlPoint controlPoint = new ControlPoint(x, y);
                controlPoints[j][i]=controlPoint;
                x+=spacing;
            }
            y+=spacing;
            x=spacing;
        }
        for(int i=1; i<this.gridDim-1; i++) {
            for (int j=1; j<this.gridDim-1; j++) {
                triangles[j][i][0] = new Triangle(controlPoints[j-1][i-1], controlPoints[j-1][i], controlPoints[j][i]);
                triangles[j][i][1] = new Triangle(controlPoints[j-1][i-1], controlPoints[j][i-1], controlPoints[j][i]);
            }
        }
        int borderY=0;
        for(int i=0; i<this.gridDim; i++){
            if(i==0){
                triangles[0][i][0] = new Triangle(new Point(0, borderY), new Point(0, borderY + spacing), controlPoints[0][i]);
                triangles[0][i][1] = new Triangle(new Point(0, borderY), new Point(spacing, borderY), controlPoints[0][i]);
                triangles[this.gridDim-1][i][0] = new Triangle(new Point(panelSize-spacing, borderY), controlPoints[9][i], new Point(panelSize, borderY+spacing));
                triangles[this.gridDim-1][i][1] = new Triangle(new Point(panelSize-spacing, borderY), new Point(panelSize, borderY), new Point(panelSize, borderY+spacing));
            }
            else if(i<10) {
                triangles[0][i][0] = new Triangle(new Point(0, borderY), new Point(0, borderY + spacing), controlPoints[0][i]);
                triangles[0][i][1] = new Triangle(new Point(0, borderY), controlPoints[0][i-1], controlPoints[0][i]);
                triangles[this.gridDim-1][i][0] = new Triangle(controlPoints[9][i-1], controlPoints[9][i], new Point(panelSize, borderY+spacing));
                triangles[this.gridDim-1][i][1] = new Triangle(controlPoints[9][i-1], new Point(panelSize, borderY), new Point(panelSize, borderY+spacing));
            }
            else{
                triangles[0][i][0] = new Triangle(new Point(0, borderY), new Point(0, panelSize), new Point(spacing, panelSize));
                triangles[0][i][1] = new Triangle(new Point(0, borderY), controlPoints[0][i-1], new Point(spacing, panelSize));
                triangles[this.gridDim-1][i][0] = new Triangle(controlPoints[9][i-1], new Point(borderY, panelSize), new Point(panelSize, panelSize));
                triangles[this.gridDim-1][i][1] = new Triangle(controlPoints[9][i-1], new Point(panelSize, borderY), new Point(panelSize, panelSize));
            }
            borderY+=spacing;
        }

        int borderX=spacing;
        for(int i=1; i<this.gridDim-1; i++){
            triangles[i][0][0] = new Triangle(new Point(borderX, 0), controlPoints[i-1][0], controlPoints[i][0]);
            triangles[i][0][1] = new Triangle(new Point(borderX, 0), new Point(borderX+spacing, 0), controlPoints[i][0]);
            triangles[i][this.gridDim-1][0] = new Triangle(controlPoints[i-1][9], new Point(borderX, panelSize), new Point(borderX+spacing, panelSize));
            triangles[i][this.gridDim-1][1] = new Triangle(controlPoints[i-1][9], controlPoints[i][9], new Point(borderX+spacing, panelSize));
            borderX+=spacing;
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
                for(int i=0; i<gridDim; i++){
                    for(int j=0; j<gridDim; j++){
                        if(controlPoints[j][i].getShape().contains(e.getX(), e.getY())){
                            isDragging = true;
                            pointDragged = new int[2];
                            pointDragged[0] = j;
                            pointDragged[1] = i;
                            break;
                        }
                    }
                }
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
                isDragging=false;
            }
        });

        this.addMouseListener(new MouseAdapter() {//if mouse exits JPANEL aka outside of bounds
            @Override
            public void mouseExited(MouseEvent e) {
//                super.mouseExited(e);

            }
        });

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isDragging){ //and e.getx inside panel and e.gety insdie panel
                    controlPoints[pointDragged[0]][pointDragged[1]].setXY(e.getX(), e.getY());
                    for(int i=0; i<gridDim; i++){
                        for(int j=0; j<gridDim; j++){
                            for(int k=0; k<=1; k++){
                                if(triangles[j][i][k].controlledBy(controlPoints[pointDragged[0]][pointDragged[1]])==1){
                                    triangles[j][i][k].setControlPoint(1, controlPoints[pointDragged[0]][pointDragged[1]]);
                                }
                                if(triangles[j][i][k].controlledBy(controlPoints[pointDragged[0]][pointDragged[1]])==2){
                                    triangles[j][i][k].setControlPoint(2, controlPoints[pointDragged[0]][pointDragged[1]]);
                                }
                                if(triangles[j][i][k].controlledBy(controlPoints[pointDragged[0]][pointDragged[1]])==3){
                                    triangles[j][i][k].setControlPoint(3, controlPoints[pointDragged[0]][pointDragged[1]]);
                                }
                            }
                        }
                    }
                    repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {

            }
        });
    }

    public void paintComponent(Graphics g){
        for(int i=0; i<this.gridDim-1; i++){
            for(int j=0; j<this.gridDim-1; j++){
                g.setColor(Color.BLACK);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Color.BLACK);
                if(isDragging) {
                    if (pointDragged[0] == j && pointDragged[1] == i) {
                        g2.setColor(Color.RED);
                    }
                }
                g2.fill(controlPoints[j][i].getShape());
            }
        }
        for(int i=0; i<this.gridDim; i++){
            for(int j=0; j<this.gridDim; j++){
                for(int k=0; k<=1; k++) {
                    g.drawPolygon(triangles[j][i][k].getXPoints(), triangles[j][i][k].getYPoints(), 3);
                }
            }
        }
    }
}