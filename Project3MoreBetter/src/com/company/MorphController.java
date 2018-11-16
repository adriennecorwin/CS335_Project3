package com.company;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class MorphController{
    private boolean isDragging = false; //true if point is being dragged
    private int pointDragged[]; //row and col of point being dragged in control point grid
    private double stepsX[][]; //amt x position of each control point should change per frame
    private double stepsY[][]; //amt y position of each control point should change
    private Timer previewTimer; //controls animation of control points
    private int delay; //delay for preview animation timer
    private int frames; //number of tween frames
    private int frameCount; //number of frames the animation has gone through
    private JFrame previewFrame; //frame for morph preview
    private JButton previewMorphButton; //starts morph preview
    private MorphGrid morphGridBefore; //morph grid before morph
    private MorphGrid morphGridAfter; //morph grid after morph
    private MorphGrid previewMorphGrid; //deep copy of first morph grid to show the morph preview animation

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
                    break;
                }
            }
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
        if (e.getX() < morphGrid.getPanelSize() && e.getY() < morphGrid.getPanelSize() && e.getX()> 0 && e.getY() > 0){
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
                morphGridBefore.setUpGrid();
                morphGridAfter.setUpGrid();
            }
        });

        //if morph duration is changed, reset timer with correct delay
        morphView.getMorphTimeSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                previewTimer.stop();
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
            }
        });

        //if num tween frames changed, recalcuate the x and y steps to get to end point
        //set timer to correct delay
        morphView.getMorphFrameSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                previewTimer.stop();
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

        //make deep copy of before grid for preview animation frame
        previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);

        stepsX = new double[previewMorphGrid.getGridDim()-1][previewMorphGrid.getGridDim()-1];
        stepsY = new double[previewMorphGrid.getGridDim()-1][previewMorphGrid.getGridDim()-1];

        addActionListeners(morphView);

    }


}
