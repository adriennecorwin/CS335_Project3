package com.company;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

public class MorphController{
    private boolean isDragging = false;
    private int pointDragged[];
    private double stepsX[][];
    private double stepsY[][];
    private Timer previewTimer;
    private int delay;
    private int frames;
    private int frameCount;
    private JFrame previewFrame;
    private JButton previewMorphButton;
    private MorphGrid morphGridBefore;
    private MorphGrid morphGridAfter;
    private MorphGrid previewMorphGrid;

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

    private void rubberBandingWithBoundaries(MouseEvent e, MorphGrid morphGrid){
        if (e.getX() > morphGrid.getPanelSize() && e.getY() < morphGrid.getPanelSize() & e.getYOnScreen() > morphGrid.getLocation().getY()) {
            morphGrid.updateTriangles(morphGrid.getPanelSize(), e.getY());
        }
        if (e.getY() > morphGrid.getPanelSize() && e.getX() < morphGrid.getPanelSize() & e.getXOnScreen() > morphGrid.getLocation().getX()) {
            morphGrid.updateTriangles(e.getX(), morphGrid.getPanelSize());
        }
        if (e.getX() > morphGrid.getPanelSize() && e.getY() > morphGrid.getPanelSize()) {
            morphGrid.updateTriangles(morphGrid.getPanelSize(), morphGrid.getPanelSize());
        }
        if (e.getXOnScreen() < morphGrid.getLocation().getX() && e.getY() < morphGrid.getPanelSize() & e.getYOnScreen() > morphGrid.getLocation().getY()) {
            morphGrid.updateTriangles(0, e.getY());
        }
        if (e.getYOnScreen() < morphGrid.getLocation().getY() && e.getX() < morphGrid.getPanelSize() & e.getXOnScreen() > morphGrid.getLocation().getX()) {
            morphGrid.updateTriangles(e.getX(), 0);
        }
        if (e.getXOnScreen() < morphGrid.getLocation().getX() && e.getYOnScreen() < morphGrid.getLocation().getY()) {
            morphGrid.updateTriangles(0, 0);
        }
//        else{
//            morphGrid.updateTriangles(e.getX(), e.getY());
//        }
        if (e.getX() < morphGrid.getPanelSize() && e.getY() < morphGrid.getPanelSize() && e.getXOnScreen() > morphGrid.getLocation().getX() && e.getYOnScreen() > morphGrid.getLocation().getLocation().getY()){
            morphGrid.updateTriangles(e.getX(), e.getY());
        }
    }

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

        previewTimer = new Timer(delay/frames, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movePoints();
            }
        });

        morphView.getPreviewMorphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);
                previewTimer.stop();
                makePreviewFrame();
                calcSteps(frames, previewMorphGrid);
                frameCount=0;

            }
        });

        morphView.getResetButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                morphGridBefore.setUpGrid();
                morphGridAfter.setUpGrid();
            }
        });

        morphView.getMorphTimeSlider().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                previewTimer.stop();
                morphView.getMorphTimeLabel().setText("Morph Duration: "+morphView.getMorphTimeSlider().getValue()+" seconds");
                delay = morphView.getMorphTimeSlider().getValue()*1000;
                if(delay%frames!=0) {
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

    private void calcSteps(int frames, MorphGrid previewMorphGrid){
        this.frames = frames;
        for(int i=0; i<previewMorphGrid.getGridDim()-1; i++) {
            for (int j = 0; j < previewMorphGrid.getGridDim() - 1; j++) {
                stepsX[j][i] = (morphGridAfter.getControlPoints()[j][i].getX()-previewMorphGrid.getControlPoints()[j][i].getX())/frames;
                stepsY[j][i] = (morphGridAfter.getControlPoints()[j][i].getY()-previewMorphGrid.getControlPoints()[j][i].getY())/frames;
            }
        }
    }

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

    public MorphController(MorphGrid morphGridBefore, MorphGrid morphGridAfter, MorphView morphView){
        pointDragged = new int[2];
        pointDragged[0]=-1;
        pointDragged[1]=-1;
        delay = morphView.getMorphTimeSlider().getValue()*1000;
        frames = morphView.getMorphFrameSlider().getValue();
        this.morphGridBefore = morphGridBefore;
        this.morphGridAfter = morphGridAfter;
        morphGridBefore.setPointDragged(pointDragged);
        morphGridAfter.setPointDragged(pointDragged);

        previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);

        stepsX = new double[previewMorphGrid.getGridDim()-1][previewMorphGrid.getGridDim()-1];
        stepsY = new double[previewMorphGrid.getGridDim()-1][previewMorphGrid.getGridDim()-1];

        addActionListeners(morphView);

    }


}
