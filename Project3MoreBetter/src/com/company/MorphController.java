package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RescaleOp;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;


public class MorphController{
    private boolean isDragging = false; //true if point is being dragged
    private int pointDragged[]; //row and col of point being dragged in control point grid
    private double stepsX[][]; //amt x position of each control point should change per frame
    private double stepsY[][]; //amt y position of each control point should change
    private Timer previewTimer; //controls animation of control points
    private Timer morphTimer;
    private int delay; //delay for preview animation timer
    private int frames; //number of tween frames
    private int frameCount; //number of frames the preview animation has gone through
    private int morphFrameCount; //number of frames the animation has gone through
    private JFrame previewFrame; //frame for morph preview
    private JFrame morphFrame;
    private JButton morphButton;
    private JButton previewMorphButton; //starts morph preview
    private MorphGrid morphGridBefore; //morph grid before morph
    private MorphGrid morphGridAfter; //morph grid after morph
    private MorphGrid previewMorphGrid; //deep copy of first morph grid to show the morph preview animation
    private MorphGrid morphGrid;
    private int gridDim;
    private Polygon polygonBound;
    private MorphTools morphTools;
    private BufferedImage inputImage;
    private BufferedImage outputImage;
    private BufferedImage outputImageMorph;
    private BufferedImage inputImageMorph;
    private BufferedImage tweenImageInput;
    private BufferedImage tweenImageOutput;
    private Triangle inputTris[][][];
    private Triangle outputTris[][][];
    private float alpha;
    private File outputFile;

    private float inputIntensity;
    private float outputIntensity;



    //check if the user has selected one of the control points
    //if they have, set point dragged to the position of that control point in the grid
    //set the corresponding point in both grids to be the point that is dragged (this point on both grids will be red)
    private void setUpDrag(MorphGrid currentMorphGrid, MorphGrid correspondingMorphGrid, MouseEvent e){
        for(int i=0; i<currentMorphGrid.getGridDim()-1; i++){
            for(int j=0; j<currentMorphGrid.getGridDim()-1; j++){
                if(currentMorphGrid.getControlPoints()[j][i].getShape().contains(e.getX(), e.getY())){
                    isDragging = true;
                    pointDragged = new int[2];
                    pointDragged[0] = j;
                    pointDragged[1] = i;
                    currentMorphGrid.setPointDragged(pointDragged);
                    correspondingMorphGrid.setCorrespondingPoint(pointDragged);
                    correspondingMorphGrid.setPointDragged(pointDragged);
                    currentMorphGrid.setCorrespondingPoint(pointDragged);
                    currentMorphGrid.repaint();
                    correspondingMorphGrid.repaint();
                    makePolygonBoundary(currentMorphGrid);
                    break;
                }
            }
        }
    }

    private void makePolygonBoundary(MorphGrid currentMorphGrid) {
        if (pointDragged[0] - 1 >= 0 && pointDragged[1] - 1 >= 0 && pointDragged[0] + 1 < gridDim && pointDragged[1] + 1 < gridDim) {
            int polygonXs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getX()};
            int polygonYs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);
        } else if (pointDragged[0] - 1 < 0 && pointDragged[1] - 1 < 0) {
            int polygonXs[] = {(int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV1().getX(), (int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV2().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getTriangles()[0][pointDragged[1]][0].getV2().getX()};
            int polygonYs[] = {(int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV1().getY(), (int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV2().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getTriangles()[0][pointDragged[1]][0].getV2().getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);

        } else if (pointDragged[0] - 1 < 0 && pointDragged[1] + 1 >= gridDim) {
            int polygonXs[] = {(int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV1().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][gridDim][0].getV3().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][gridDim][0].getV2().getX(), (int) currentMorphGrid.getTriangles()[0][gridDim][0].getV1().getX()};
            int polygonYs[] = {(int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV1().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][gridDim][0].getV3().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][gridDim][0].getV2().getY(), (int) currentMorphGrid.getTriangles()[0][gridDim][0].getV1().getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);

        } else if (pointDragged[0] - 1 < 0) {
            int polygonXs[] = {(int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV1().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getTriangles()[0][pointDragged[0]][0].getV2().getX()};
            int polygonYs[] = {(int) currentMorphGrid.getTriangles()[0][pointDragged[1]][1].getV1().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1]].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] + 1][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getTriangles()[0][pointDragged[1]+1][0].getV1().getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);

        } else if (pointDragged[0] + 1 >= gridDim && pointDragged[1] + 1 >= gridDim) {
            int polygonXs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV2().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV3().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][0].getV2().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getX()};
            int polygonYs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV2().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV3().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][0].getV2().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);

        }  else if (pointDragged[0] + 1 >= gridDim && pointDragged[1]-1<0) {
            int polygonXs[] = {(int) currentMorphGrid.getTriangles()[pointDragged[0]][0][0].getV1().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0]+1][0][0].getV1().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1]][1].getV3().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV3().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getX()};
            int polygonYs[] = {(int) currentMorphGrid.getTriangles()[pointDragged[0]][0][0].getV1().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0]+1][0][0].getV1().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1]][1].getV3().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV3().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);

        } else if (pointDragged[0] + 1 >= gridDim) {
            int polygonXs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV2().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV3().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getX()};
            int polygonYs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] - 1].getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV2().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0] + 1][pointDragged[1] + 1][1].getV3().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0] - 1][pointDragged[1]].getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);
        }
        else if (pointDragged[1]-1<0) {
            int polygonXs[] = {(int) currentMorphGrid.getTriangles()[pointDragged[0]][0][0].getV1().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0]][pointDragged[1]][1].getV2().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]+1][pointDragged[1]].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]+1][pointDragged[1]+1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]-1][pointDragged[1]].getX()};
            int polygonYs[] = {(int) currentMorphGrid.getTriangles()[pointDragged[0]][0][0].getV1().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0]][pointDragged[1]][1].getV2().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]+1][pointDragged[1]].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]+1][pointDragged[1]+1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1] + 1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]-1][pointDragged[1]].getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);
        }
        else if (pointDragged[1]+1>=gridDim) {
            int polygonXs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0]-1][pointDragged[1]-1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1]-1].getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]+1][pointDragged[1]].getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0]+1][pointDragged[1] + 1][0].getV3().getX(), (int) currentMorphGrid.getTriangles()[pointDragged[0]+1][pointDragged[1] + 1][0].getV2().getX(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]-1][pointDragged[1]].getX()};
            int polygonYs[] = {(int) currentMorphGrid.getControlPoints()[pointDragged[0]-1][pointDragged[1]-1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]][pointDragged[1]-1].getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]+1][pointDragged[1]].getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0]+1][pointDragged[1] + 1][0].getV3().getY(), (int) currentMorphGrid.getTriangles()[pointDragged[0]+1][pointDragged[1] + 1][0].getV2().getY(), (int) currentMorphGrid.getControlPoints()[pointDragged[0]-1][pointDragged[1]].getY()};
            polygonBound = new Polygon(polygonXs, polygonYs, 6);
        }
    }



    //force dragged point to stay in panel bounds
    //as the user moves the mouse (drags), redraw the point and the triangles it controls
    private void rubberBandingWithBoundaries(MouseEvent e, MorphGrid morphGrid){

        if (e.getX() > morphGrid.getPanelSize() && e.getY() < morphGrid.getPanelSize() & e.getYOnScreen() > morphGrid.getLocation().getY()) {
            morphGrid.updateTriangles(morphGrid.getPanelSize(), e.getY());
        }
        if (e.getX() > morphGrid.getPanelSize() && e.getY() < 0) {
            morphGrid.updateTriangles(morphGrid.getPanelSize(), 0);
        }
        if (e.getY() > morphGrid.getPanelSize() && e.getX() < morphGrid.getPanelSize() & e.getXOnScreen() > morphGrid.getLocation().getX()) {
            morphGrid.updateTriangles(e.getX(), morphGrid.getPanelSize());
        }
        if (e.getX() > morphGrid.getPanelSize() && e.getY() > morphGrid.getPanelSize()) {
            morphGrid.updateTriangles(morphGrid.getPanelSize(), morphGrid.getPanelSize());
        }
        if (e.getX() < 0 && e.getY() < morphGrid.getPanelSize() & e.getY() > 0) {
            morphGrid.updateTriangles(0, e.getY());
        }
        if (e.getY() < 0 && e.getX() < morphGrid.getPanelSize() & e.getX() > 0) {
            morphGrid.updateTriangles(e.getX(), 0);
        }
        if (e.getX() < 0 && e.getY() < 0) {
            morphGrid.updateTriangles(0, 0);
        }
        if (e.getX() < morphGrid.getPanelSize() && e.getY() < morphGrid.getPanelSize() && e.getX()> 0 && e.getY() > 0 && polygonBound.contains(new Point(e.getX(), e.getY()))){
            morphGrid.updateTriangles(e.getX(), e.getY());
        }
    }

    //for preview morph animation
    //moves control points 1/frames steps closer to corresponding point in the second morph grid
    //redraw triangles controlled by these control points at every step
    //once it has moved frames/frames steps, should be at target position
    //if not bc of floating point error, move to correct ending position
    private void movePoints(){
        frameCount++;
        if(frameCount<frames) {
            for (int i = 0; i < previewMorphGrid.getGridDim() - 1; i++) {
                for (int j = 0; j < previewMorphGrid.getGridDim() - 1; j++) {
                    if (stepsX[j][i] != 0 || stepsY[j][i] != 0) {
                        previewMorphGrid.getControlPoints()[j][i].setXY((int) (morphGridBefore.getControlPoints()[j][i].getX() + stepsX[j][i]*frameCount), (int) (morphGridBefore.getControlPoints()[j][i].getY() + stepsY[j][i]*frameCount));
                        previewMorphGrid.updateTrianglePreview(j, i, previewMorphGrid.getControlPoints()[j][i]);
                    }
                }
            }
        }
        else{
            for (int i = 0; i < previewMorphGrid.getGridDim() - 1; i++) {
                for (int j = 0; j < previewMorphGrid.getGridDim() - 1; j++) {
                    if (previewMorphGrid.getControlPoints()[j][i].getX() != morphGridAfter.getControlPoints()[j][i].getX() || previewMorphGrid.getControlPoints()[j][i].getY() != morphGridAfter.getControlPoints()[j][i].getY()) {
                        previewMorphGrid.getControlPoints()[j][i].setXY((int) morphGridAfter.getControlPoints()[j][i].getX(), (int) morphGridAfter.getControlPoints()[j][i].getY());
                        previewMorphGrid.updateTrianglePreview(j, i, previewMorphGrid.getControlPoints()[j][i]);
                    }
                }
            }
            previewTimer.stop();
        }
    }

    private void animateWarpTriangles(){
        morphFrameCount++;
        alpha+=(float)1/frames;
        morphGrid.setAlpha(alpha);
        if(morphFrameCount<frames){
            for (int i = 0; i < morphGrid.getGridDim() - 1; i++) {
                for (int j = 0; j < morphGrid.getGridDim() - 1; j++) {
                    if (stepsX[j][i] != 0 || stepsY[j][i] != 0) {
                        morphGrid.getControlPoints()[j][i].setXY((int) (morphGridBefore.getControlPoints()[j][i].getX() + stepsX[j][i] * morphFrameCount), (int) (morphGridBefore.getControlPoints()[j][i].getY() + stepsY[j][i] * morphFrameCount));
                        morphGrid.updateTrianglePreview(j, i, morphGrid.getControlPoints()[j][i]);
                    }
                }
            }

            for(int i=0; i<morphGrid.getGridDim(); i++) {
                for (int j = 0; j < morphGrid.getGridDim(); j++) {
                    for (int k = 0; k <= 1; k++) {
                        if (inputTris[j][i][k] != null) {
                            if (inputTris[j][i][k].getV1().getX() != morphGrid.getTriangles()[j][i][k].getV1().getX() || inputTris[j][i][k].getV2().getX() != morphGrid.getTriangles()[j][i][k].getV2().getX() || inputTris[j][i][k].getV3().getX() != morphGrid.getTriangles()[j][i][k].getV3().getX() || inputTris[j][i][k].getV1().getY() != morphGrid.getTriangles()[j][i][k].getV1().getY() || inputTris[j][i][k].getV2().getY() != morphGrid.getTriangles()[j][i][k].getV2().getY() || inputTris[j][i][k].getV3().getY() != morphGrid.getTriangles()[j][i][k].getV3().getY()) {
                                morphTools.warpTriangle(inputImageMorph, tweenImageInput, inputTris[j][i][k], morphGrid.getTriangles()[j][i][k], null, null);
                                morphTools.warpTriangle(outputImageMorph, tweenImageOutput, outputTris[j][i][1-k], morphGrid.getTriangles()[j][i][1-k], null, null);
                                morphGrid.setOutputImage(tweenImageOutput);
                                morphGrid.setImage(tweenImageInput);
                                morphGrid.repaint();
                            }

                        }
                    }
                }
            }
            //https://examples.javacodegeeks.com/desktop-java/imageio/create-image-file-from-graphics-object/
//            File file = new File("tween"+morphFrameCount+".jpg");
//            try{
//                ImageIO.write(tweenImage, "jpg", file);
//            }
//            catch (IOException e1){}
//            try{
//                tweenImage = ImageIO.read(file);
//                morphGrid.setImage(tweenImage);
//            }
//            catch (IOException e1){}
        }
        else{
            for (int i = 0; i < morphGrid.getGridDim() - 1; i++) {
                for (int j = 0; j < morphGrid.getGridDim() - 1; j++) {
                    if (morphGrid.getControlPoints()[j][i].getX() != morphGridAfter.getControlPoints()[j][i].getX() || morphGrid.getControlPoints()[j][i].getY() != morphGridAfter.getControlPoints()[j][i].getY()) {
                        morphGrid.getControlPoints()[j][i].setXY((int) morphGridAfter.getControlPoints()[j][i].getX(), (int) morphGridAfter.getControlPoints()[j][i].getY());
                        morphGrid.updateTrianglePreview(j, i, morphGrid.getControlPoints()[j][i]);
                    }
                }
            }
            for(int i=0; i<morphGrid.getGridDim(); i++) {
                for (int j = 0; j < morphGrid.getGridDim(); j++) {
                    for (int k = 0; k <= 1; k++) {
                        if (inputTris[j][i][k] != null) {
                            if (inputTris[j][i][k].getV1().getX() != morphGrid.getTriangles()[j][i][k].getV1().getX() || inputTris[j][i][k].getV2().getX() != morphGrid.getTriangles()[j][i][k].getV2().getX() || inputTris[j][i][k].getV3().getX() != morphGrid.getTriangles()[j][i][k].getV3().getX() || inputTris[j][i][k].getV1().getY() != morphGrid.getTriangles()[j][i][k].getV1().getY() || inputTris[j][i][k].getV2().getY() != morphGrid.getTriangles()[j][i][k].getV2().getY() || inputTris[j][i][k].getV3().getY() != morphGrid.getTriangles()[j][i][k].getV3().getY()) {
                                morphTools.warpTriangle(inputImageMorph, tweenImageInput, inputTris[j][i][k], outputTris[j][i][k], null, null);
                                morphTools.warpTriangle(outputImageMorph, tweenImageOutput, outputTris[j][i][1-k], outputTris[j][i][1-k], null, null);
                                morphGrid.setOutputImage(tweenImageOutput);
                                morphGrid.setImage(tweenImageInput);
                                morphGrid.repaint();
                            }

                        }
                    }
                }
            }
            morphGrid.setAlpha(1);
            morphGrid.repaint();

            morphTimer.stop();
        }
    }

    private void copyTriangles(){
        for(int i=0; i<morphGrid.getGridDim(); i++) {
            for (int j = 0; j < morphGrid.getGridDim(); j++) {
                for (int k = 0; k <= 1; k++) {
                    inputTris[j][i][k]= new Triangle(new ControlPoint((int)morphGridBefore.getTriangles()[j][i][k].getV1().getX(), (int)morphGridBefore.getTriangles()[j][i][k].getV1().getY()), new ControlPoint((int)morphGridBefore.getTriangles()[j][i][k].getV2().getX(), (int)morphGridBefore.getTriangles()[j][i][k].getV2().getY()), new ControlPoint((int)morphGridBefore.getTriangles()[j][i][k].getV3().getX(), (int)morphGridBefore.getTriangles()[j][i][k].getV3().getY()));
                    outputTris[j][i][k]= new Triangle(new ControlPoint((int)morphGridAfter.getTriangles()[j][i][k].getV1().getX(), (int)morphGridAfter.getTriangles()[j][i][k].getV1().getY()), new ControlPoint((int)morphGridAfter.getTriangles()[j][i][k].getV2().getX(), (int)morphGridAfter.getTriangles()[j][i][k].getV2().getY()), new ControlPoint((int)morphGridAfter.getTriangles()[j][i][k].getV3().getX(), (int)morphGridAfter.getTriangles()[j][i][k].getV3().getY()));

                }
            }
        }

    }

    private void addActionListeners(MorphView morphView){

        //add mouse listeners for both morph grid panels for dragging and rubberbanding
        morphGridBefore.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setUpDrag(morphGridBefore, morphGridAfter, e);
            }
        });

        morphGridBefore.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging=false;
            }
        });

        morphGridBefore.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isDragging){
                    rubberBandingWithBoundaries(e, morphGridBefore);
                }
            }
        });
        morphGridAfter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                setUpDrag(morphGridAfter, morphGridBefore, e);
            }
        });

        morphGridAfter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging=false;
            }
        });

        morphGridAfter.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isDragging){
                    rubberBandingWithBoundaries(e, morphGridAfter);
                }
            }
        });

        //initialize timer for preview animation
        previewTimer = new Timer(delay/frames, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePoints();
            }
        });

        morphTimer = new Timer(delay / frames, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animateWarpTriangles();
            }
        });

        //open preview window when preview morph button clicked
        morphView.getPreviewMorphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore); //make deep copy of before grid for preview frame
                previewTimer.stop();
                makePreviewFrame();
                calcSteps(frames, previewMorphGrid); //calculate x and y steps to go from starting position to ending position
                frameCount=0;
            }
        });

        //reset grids to their original states
        morphView.getResetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                morphGridBefore.setUpGrid(gridDim);
                morphGridAfter.setUpGrid(gridDim);
            }
        });

        morphView.getGridResSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                gridDim = morphView.getGridResSlider().getValue();
                morphGridBefore.setUpGrid(gridDim);
                morphGridAfter.setUpGrid(gridDim);
                morphGridBefore.revalidate();
                morphGridAfter.revalidate();
                morphView.getGridResLabel().setText("Grid Resolution: "+morphView.getGridResSlider().getValue()+"x"+morphView.getGridResSlider().getValue());
            }
        });

        //if morph duration is changed, reset timer with correct delay
        morphView.getMorphTimeSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                previewTimer.stop();
                morphTimer.stop();
                morphView.getMorphTimeLabel().setText("Morph Duration: "+morphView.getMorphTimeSlider().getValue()+" seconds");
                delay = morphView.getMorphTimeSlider().getValue()*1000; //(*1000 for milliseconds)
                if(delay%frames!=0) { //(for floating point to int error)
                    delay = delay + 1;
                }
                previewTimer = new Timer(delay/frames, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        movePoints();
                    }
                });
                previewTimer.start();
                morphTimer = new Timer(delay/frames, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        animateWarpTriangles();
                    }
                });
//                morphTimer.start();
            }
        });

        //if num tween frames changed, recalcuate the x and y steps to get to end point
        //set timer to correct delay
        morphView.getMorphFrameSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                previewTimer.stop();
                morphTimer.stop();
                frames = morphView.getMorphFrameSlider().getValue();
                morphView.getMorphFrameLabel().setText("Frames Per Second: "+frames);
                calcSteps(frames, previewMorphGrid);
                previewTimer = new Timer(delay/frames, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        movePoints();
                    }
                });
                previewTimer.start();
                morphTimer = new Timer(delay/frames, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        animateWarpTriangles();
                    }
                });
//                morphTimer.start();
            }
        });

        morphView.getFileOpenInput().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = morphView.getFc().showOpenDialog(morphView);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = morphView.getFc().getSelectedFile();
                    try {
                        inputImage = ImageIO.read(file);
                        inputImageMorph = ImageIO.read(file);
                        morphGridBefore.setImage(inputImage);
                    } catch (IOException e1){}
                }
            }
        });

        morphView.getFileOpenOutput().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = morphView.getFc().showOpenDialog(morphView);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = morphView.getFc().getSelectedFile();
                    try {
                        outputImage = ImageIO.read(file);
                        outputImageMorph = ImageIO.read(file);
                        morphGridAfter.setImage(outputImage);
                    } catch (IOException e1){}
                }
            }
        });

        morphView.getMorphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                morphTimer.stop();
                morphFrameCount = 0;
                makeMorphFrame();
            }
        });

        morphView.getInputIntensitySlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                BufferedImage inputImageCopy;
                ColorModel cm = inputImage.getColorModel();
                boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
                WritableRaster raster = inputImage.copyData(null);
                inputImageCopy = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
                inputIntensity = morphView.getInputIntensitySlider().getValue();
                RescaleOp rescaleOp = new RescaleOp(inputIntensity/100, 0, null);
                inputImage = rescaleOp.filter(inputImageCopy, inputImageCopy);
                morphGridBefore.setImage(inputImageCopy);
            }
        });

        morphView.getOutputIntensitySlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                outputIntensity = morphView.getOutputIntensitySlider().getValue();
                RescaleOp rescaleOp = new RescaleOp(outputIntensity/100, 0, null);
                outputImage = rescaleOp.filter(outputImage, outputImage);
                morphGridAfter.setImage(outputImage);
            }
        });

    }

    //creates the preview morph frame with preview morph button to start animation
    private void makePreviewFrame(){
        previewFrame = new JFrame("Preview Morph");
        previewFrame.setLayout(new FlowLayout());
        previewMorphButton = new JButton("Preview Morph");
        previewFrame.getContentPane().add(previewMorphButton);
        previewFrame.getContentPane().add(previewMorphGrid);
        previewFrame.setSize(700, 700);
        previewFrame.setVisible(true);

        previewMorphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameCount=0;
                animatePreview();
            }
        });
    }

    private void makeMorphFrame(){
        morphGrid = new MorphGrid(morphGridBefore);
        morphFrame = new JFrame("Morph");
        morphButton = new JButton("Morph");
        morphFrame.setLayout(new FlowLayout());
        morphFrame.getContentPane().add(morphGrid);
        morphFrame.getContentPane().add(morphButton);
        morphFrame.setSize(700, 700);
        morphFrame.setVisible(true);

        morphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                morphFrameCount = 0;
                ColorModel cm = inputImageMorph.getColorModel();
                boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
                WritableRaster raster = inputImageMorph.copyData(null);
                tweenImageInput = new BufferedImage(cm, raster, isAlphaPremultiplied, null);
                ColorModel cm2 = outputImageMorph.getColorModel();
                boolean isAlphaPremultiplied2 = cm2.isAlphaPremultiplied();
                WritableRaster raster2 = outputImageMorph.copyData(null);
                tweenImageOutput = new BufferedImage(cm2, raster2, isAlphaPremultiplied2, null);
                morphFrame.getContentPane().removeAll();
                morphGrid = new MorphGrid(MorphController.this.morphGridBefore);
                morphGrid.setOutputImage(outputImageMorph);
                morphFrame.getContentPane().add(morphGrid);
                morphFrame.getContentPane().add(morphButton);
                calcSteps(frames, morphGrid);
                morphGrid.repaint();
                morphGrid.revalidate();
                morphGrid.setVisible(true);
                alpha = 0;
                morphGrid.setAlpha(0);
                copyTriangles();
                morphTimer.start();
            }
        });
    }

    //calculate distance between each point in before grid and each point in after grid
    //divide by number of frames specified to get how much x and y should move per timer fire to make animation
    private void calcSteps(int frames, MorphGrid previewMorphGrid){
        this.frames = frames;
        for(int i=0; i<previewMorphGrid.getGridDim()-1; i++) {
            for (int j = 0; j < previewMorphGrid.getGridDim() - 1; j++) {
                stepsX[j][i] = (morphGridAfter.getControlPoints()[j][i].getX()-previewMorphGrid.getControlPoints()[j][i].getX())/frames;
                stepsY[j][i] = (morphGridAfter.getControlPoints()[j][i].getY()-previewMorphGrid.getControlPoints()[j][i].getY())/frames;
            }
        }
    }



    //sets up and starts the animation
    //every time preview morph button is clicked grid will start back at original positions of preview grid
    //and carry out animation again
    private void animatePreview(){
        previewTimer.stop();
        previewFrame.getContentPane().remove(previewMorphGrid);
        previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);
        calcSteps(frames, previewMorphGrid);
        previewFrame.getContentPane().add(previewMorphGrid);
        previewMorphGrid.repaint();
        previewMorphGrid.revalidate();
        previewFrame.setVisible(true);
        previewTimer.start();
    }

    //CONTSTRUCTOR
    public MorphController(MorphGrid morphGridBefore, MorphGrid morphGridAfter, MorphView morphView){

        //initialize variables
        pointDragged = new int[2];
        pointDragged[0]=-1;
        pointDragged[1]=-1;
        delay = morphView.getMorphTimeSlider().getValue()*1000;
        frames = morphView.getMorphFrameSlider().getValue();
        this.morphGridBefore = morphGridBefore;
        this.morphGridAfter = morphGridAfter;
        morphGridBefore.setPointDragged(pointDragged);
        morphGridAfter.setPointDragged(pointDragged);

        try {
            inputImage = ImageIO.read(new File("spoon.jpg"));
            inputImageMorph = ImageIO.read(new File("spoon.jpg"));
            tweenImageInput = ImageIO.read(new File("spoon.jpg"));
            outputImage = ImageIO.read(new File("spoon2.jpg"));
            outputImageMorph = ImageIO.read(new File("spoon2.jpg"));
            tweenImageOutput = ImageIO.read(new File("spoon2.jpg"));

            morphGridBefore.setImage(inputImage);
            morphGridAfter.setImage(outputImage);
        }
        catch (IOException e1){}

        //make deep copy of before grid for preview animation frame
        previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);

        stepsX = new double[previewMorphGrid.getGridDim()-1][previewMorphGrid.getGridDim()-1];
        stepsY = new double[previewMorphGrid.getGridDim()-1][previewMorphGrid.getGridDim()-1];

        gridDim = morphView.getGridResSlider().getValue();

        morphTools = new MorphTools();
        inputTris = new Triangle[gridDim+1][gridDim+1][2];
        outputTris = new Triangle[gridDim+1][gridDim+1][2];

        morphGridBefore.setImage(inputImage);
        morphGridAfter.setImage(outputImage);

        morphGridAfter.repaint();
        addActionListeners(morphView);
    }




}
