package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;



public class MorphGrid extends JPanel {
    private int gridDim; //number of control points in each row and col
    private int panelSize; //size of each morph grid panel
    private int spacing; //space between each control point
    private int pointDragged[]; //row and col of point being dragged
    private boolean isCopy=false; //true if instance is a deep copy
    private ControlPoint correspondingPoint; //corresponding point of point selected from other grid
    private ControlPoint controlPoints[][]; // array of control points
    private Triangle triangles[][][]; //array for triangles indicating row, col, and upper or lower triangle
    private BufferedImage image;
    private BufferedImage outputImage;
    private Boolean isMorphing;
    private float alpha;
    private int tweenCount;
    private Boolean isMorphGrid;


    //create gridDim*gridDim control points with positions spaced equally apart in panel
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

//        int ybord=0;
//        for(int i=0; i<=gridDim; i++){
//            ControlPoint controlPoint1 = new ControlPoint(new Point(0, ybord));
//            ControlPoint controlPoint2 = new ControlPoint(new Point(panelSize, ybord));
//
//            controlPoint1.setRowCol(0, i);
//            controlPoint2.setRowCol(gridDim, i);
//
//            controlPoints[0][i] = controlPoint1;
//            controlPoints[gridDim][i] = controlPoint2;
//            ybord+=spacing;
//        }
//
//        int xbord=0;
//        for(int i=1; i<=gridDim; i++){
//            ControlPoint controlPoint1 = new ControlPoint(new Point(i, xbord));
//            ControlPoint controlPoint2 = new ControlPoint(new Point(i, panelSize));
//
//            controlPoint1.setRowCol(i, 0);
//            controlPoint2.setRowCol(i, gridDim);
//
//            controlPoints[i][0] = controlPoint1;
//            controlPoints[i][gridDim] = controlPoint2;
//            xbord+=spacing;
//        }

    }


    //create triangles from control points
    private void setUpTriangles(ControlPoint controlPoints[][]){

        //make all interior triangles
        for(int i=1; i<this.gridDim-1; i++) {
            for (int j=1; j<this.gridDim-1; j++) {
                triangles[j][i][0] = new Triangle(controlPoints[j-1][i-1], controlPoints[j-1][i], controlPoints[j][i]);
                triangles[j][i][1] = new Triangle(controlPoints[j-1][i-1], controlPoints[j][i-1], controlPoints[j][i]);
            }
        }

//        for(int i=0; i<this.gridDim; i++) {
//            for (int j=0; j<this.gridDim; j++) {
//                triangles[j][i][0] = new Triangle(controlPoints[j][i], controlPoints[j+1][i], controlPoints[j+1][i+1]);
//                triangles[j][i][1] = new Triangle(controlPoints[j][i], controlPoints[j][i+1], controlPoints[j+1][i+1]);
//            }
//        }

        //make border triangles

        //make triangles for col 0 and col gridDim-1
        int borderY=0;
        for(int i=0; i<this.gridDim; i++){
            if(i==0){
                triangles[0][i][0] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(0, borderY + spacing)), controlPoints[0][i]);
                triangles[0][i][1] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(spacing, borderY)), controlPoints[0][i]);
                triangles[this.gridDim-1][i][0] = new Triangle(new ControlPoint(new Point(spacing*(this.getGridDim()-1), borderY)), controlPoints[this.gridDim-2][i], new ControlPoint(new Point(spacing*this.getGridDim(), borderY+spacing)));
                triangles[this.gridDim-1][i][1] = new Triangle(new ControlPoint(new Point(spacing*(this.getGridDim()-1), borderY)), new ControlPoint(new Point(spacing*this.getGridDim(), borderY)), new ControlPoint(new Point(spacing*this.getGridDim(), borderY+spacing)));
            }
            else if(i<this.gridDim-1) {
                triangles[0][i][0] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(0, borderY + spacing)), controlPoints[0][i]);
                triangles[0][i][1] = new Triangle(new ControlPoint(new Point(0, borderY)), controlPoints[0][i-1], controlPoints[0][i]);
                triangles[this.gridDim-1][i][0] = new Triangle(controlPoints[this.gridDim-2][i-1], controlPoints[this.gridDim-2][i], new ControlPoint(new Point(spacing*this.getGridDim(), borderY+spacing)));
                triangles[this.gridDim-1][i][1] = new Triangle(controlPoints[this.gridDim-2][i-1], new ControlPoint(new Point(spacing*this.getGridDim(), borderY)), new ControlPoint(new Point(spacing*this.getGridDim(), borderY+spacing)));
            }
            else{
                triangles[0][i][0] = new Triangle(new ControlPoint(new Point(0, borderY)), new ControlPoint(new Point(0, spacing*this.getGridDim())), new ControlPoint(new Point(spacing, spacing*this.getGridDim())));
                triangles[0][i][1] = new Triangle(new ControlPoint(new Point(0, borderY)), controlPoints[0][i-1], new ControlPoint(new Point(spacing, spacing*this.getGridDim())));
                triangles[this.gridDim-1][i][0] = new Triangle(controlPoints[this.gridDim-2][i-1], new ControlPoint(new Point(borderY, spacing*this.getGridDim())), new ControlPoint(new Point(spacing*this.getGridDim(), spacing*this.getGridDim())));
                triangles[this.gridDim-1][i][1] = new Triangle(controlPoints[this.gridDim-2][i-1], new ControlPoint(new Point(spacing*this.getGridDim(), borderY)), new ControlPoint(new Point(spacing*this.getGridDim(), spacing*this.getGridDim())));
            }
            borderY+=spacing;
        }

        //make triangles for row 0 and row gridDim-1
        //excluding corners since they were already drawn for col 0 and col gridDim-1
        int borderX=spacing;
        for(int i=1; i<this.gridDim-1; i++){
            triangles[i][0][0] = new Triangle(new ControlPoint(new Point(borderX, 0)), controlPoints[i-1][0], controlPoints[i][0]);
            triangles[i][0][1] = new Triangle(new ControlPoint(new Point(borderX, 0)), new ControlPoint(new Point(borderX+spacing, 0)), controlPoints[i][0]);
            triangles[i][this.gridDim-1][0] = new Triangle(controlPoints[i-1][this.gridDim-2], new ControlPoint(new Point(borderX, spacing*this.getGridDim())), new ControlPoint(new Point(borderX+spacing, spacing*this.getGridDim())));
            triangles[i][this.gridDim-1][1] = new Triangle(controlPoints[i-1][this.gridDim-2], controlPoints[i][this.gridDim-2], new ControlPoint(new Point(borderX+spacing, spacing*this.getGridDim())));
            borderX+=spacing;
        }
    }

    //CONSTRUCTOR
    public MorphGrid(int gridDim){
        panelSize = 605;
        this.setPreferredSize(new Dimension(panelSize,panelSize));
        this.gridDim=gridDim+1;
        spacing = panelSize/this.gridDim;
        pointDragged = new int[2];
        outputImage = null;
        alpha=0;
        setUpGrid(gridDim);
        isMorphing =false;
        tweenCount = 0;
        isMorphGrid = false;
    }

    //DEEP COPY CONSTRUCTOR
    MorphGrid(MorphGrid toCopy){
        this.setPreferredSize(new Dimension(605, 605));
        this.gridDim = toCopy.gridDim;
        this.panelSize = toCopy.panelSize;
        this.spacing = toCopy.spacing;
        this.outputImage = null;
        //http://www.javased.com/?post=3514158
        ColorModel cm = toCopy.image.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = toCopy.image.copyData(null);
        this.image = new BufferedImage(cm, raster, isAlphaPremultiplied, null);

        this.correspondingPoint = toCopy.correspondingPoint;
        this.controlPoints = new ControlPoint[this.gridDim-1][this.gridDim-1];
        for(int i=0; i<this.gridDim-1; i++){
            for(int j=0; j<this.gridDim-1; j++){
                ControlPoint controlPoint = new ControlPoint((int)toCopy.controlPoints[j][i].getX(), (int)toCopy.controlPoints[j][i].getY());
                controlPoint.setRowCol(j, i);
                this.controlPoints[j][i]= controlPoint;

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
        this.alpha=0;
        this.isMorphing = false;
        this.tweenCount = 0;
        isMorphGrid = false;
    }

    //paints triangles and control points in panel
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.BLACK);

        //https://stackoverflow.com/questions/11552092/changing-image-opacity
        if(outputImage!=null) {
            if(alpha<=1) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            }
            g2.drawImage(outputImage, 0, 0, this);
        }
        if(alpha>=0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)1-alpha));
        }
        g2.drawImage(image, 0, 0, this);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1));


        if(isMorphGrid) {
            if (isMorphing) {
                ColorModel cm = image.getColorModel();
                boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
                WritableRaster raster = image.copyData(null);
                BufferedImage compositeTween = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
                Graphics2D saveMorph = compositeTween.createGraphics();
                if (outputImage != null) {
                    if (alpha <= 1) {
                        saveMorph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    }
                    saveMorph.drawImage(outputImage, 0, 0, this);
                }
                if (alpha >= 0) {
                    saveMorph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float) 1 - alpha));
                }
                saveMorph.drawImage(image, 0, 0, this);
                File file = new File("tween" + tweenCount + ".jpg");
                try {
                    ImageIO.write(compositeTween, "jpg", file);
                } catch (IOException e1) {
                }
//            try{
//                tweenImage = ImageIO.read(file);
//                morphGrid.setImage(tweenImage);
//            }
//            catch (IOException e1){}
            }
        }

        else {
            for (int i = 0; i < this.gridDim - 1; i++) {
                for (int j = 0; j < this.gridDim - 1; j++) {
                    if (pointDragged[0] == j && pointDragged[1] == i && !isCopy) {
                        g2.setColor(Color.RED);
                        g2.fill(controlPoints[j][i].getShape());
                        g2.setColor(Color.BLACK);
                    } else if (controlPoints[j][i] == correspondingPoint && !isCopy) {
                        g2.setColor(Color.RED);
                        g2.fill(controlPoints[j][i].getShape());
                        g2.setColor(Color.BLACK);
                    } else {
                        g2.fill(controlPoints[j][i].getShape());
                    }
                }
            }

            g.setColor(Color.BLACK);

            //draw triangles as 3 point polygons with xpoints as all x points from 3 control points triangle is controlled by
            //and y points as ypoints
            for (int i = 0; i < this.gridDim; i++) {
                for (int j = 0; j < this.gridDim; j++) {
                    for (int k = 0; k <= 1; k++) {
                        g2.drawPolygon(triangles[j][i][k].getXPoints(), triangles[j][i][k].getYPoints(), 3);
                    }
                }
            }
        }
    }

    //initializes the morph grid with triangles and control points
    public void setUpGrid(int gridDim){
        this.gridDim = gridDim+1;
        controlPoints = new ControlPoint[this.gridDim-1][this.gridDim-1];
        triangles = new Triangle[this.gridDim][this.gridDim][2];

        setUpControlPoints();
        setUpTriangles(controlPoints);

        repaint();
    }

    //update triangles when point is dragged
    //set the position of control point that is being dragged to x and y position of mouse
    //go through each triangle and if it is controlled by the control point being dragged
    //update that vertex to new position and redraw triangle
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

    //update triangles when preview animation of morph happens
    //if triangle is controlled by the control point at the rowth row and colth col of control point array grid
    //(need row and col instead of control point bc the control point's position will change but its row and col pos in array won't
    //so this ensures we update same control point even when it has different x, y position)
    //update that vertex to new position and redraw triangle
    public Triangle[][][] updateTrianglePreview(int row, int col, ControlPoint after){
        Triangle inputTris[][][] = new Triangle[gridDim][gridDim][2];
        for(int i=0; i<gridDim; i++) {
            for (int j = 0; j < gridDim; j++) {
                for (int k = 0; k <= 1; k++) {
                    inputTris[j][i][k] = new Triangle(triangles[j][i][k].getV1(), triangles[j][i][k].getV2(), triangles[j][i][k].getV3());
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
        return inputTris;
    }


    //GETTERS AND SETTERS
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

    public Triangle[][][] getTriangles(){
        return triangles;
    }

    public int getPanelSize(){
        return panelSize;
    }

    public void setImage(BufferedImage image){
        this.image = image;
        repaint();
    }

    public void setOutputImage(BufferedImage outputImage){
        this.outputImage = outputImage;
    }

    public BufferedImage getImage(){
        return image;
    }

    public void setAlpha(float alpha){
        this.alpha = alpha;
    }

    public void setIsMorphing(boolean isMorphing){
        this.isMorphing = isMorphing;
    }

    public void setTweenCount(int tweenCount){
        this.tweenCount = tweenCount;
    }

    public void setIsMorphGrid(boolean isMorphGrid){
        this.isMorphGrid = isMorphGrid;
    }

    public int getPanelDim(){
        return spacing*this.gridDim;
    }

}
