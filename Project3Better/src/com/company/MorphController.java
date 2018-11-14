package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MorphController{
    private boolean isDragging = false;
    private int pointDragged[];
    private double stepsX[][];
    private double stepsY[][];
    private Timer previewTimer;
    private int frames;
    private int frameCount=0;
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
                    currentMorphGrid.repaint();
                    correspondingMorphGrid.repaint();
                    //currentMorphGrid.revalidate();
                    //correspondingMorphGrid.revalidate();
                    break;
                }
            }
        }
    }

    private void rubberBandingWithBoundaries(MouseEvent e, MorphGrid morphGrid){
        if(e.getX()>605 && e.getY()<605 & e.getYOnScreen()>morphGrid.getLocation().getY()){
            morphGrid.updateTriangles(605, e.getY());
        }
        else if(e.getY()>605 && e.getX()<605 & e.getXOnScreen()>morphGrid.getLocation().getX()) {
            morphGrid.updateTriangles(e.getX(), 605);
        }
        else if(e.getX()>605 && e.getY()>605){
            morphGrid.updateTriangles(605, 605);
        }
        else if(e.getXOnScreen()<morphGrid.getLocation().getX() && e.getY()<605 & e.getYOnScreen()>morphGrid.getLocation().getY()){
            morphGrid.updateTriangles(0, e.getY());
        }
        else if(e.getYOnScreen()<morphGrid.getLocation().getY() && e.getX()<605 & e.getXOnScreen()>morphGrid.getLocation().getX()){
            morphGrid.updateTriangles(e.getX(), 0);
        }
        else if (e.getXOnScreen()<morphGrid.getLocation().getX() && e.getYOnScreen()<morphGrid.getLocation().getY()){
            morphGrid.updateTriangles(0, 0);
        }
        else{
            morphGrid.updateTriangles(e.getX(), e.getY());
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

        previewTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameCount++;
                if (frameCount <= frames) {
                    for (int i = 0; i < previewMorphGrid.getGridDim() - 1; i++) {
                        for (int j = 0; j < previewMorphGrid.getGridDim() - 1; j++) {
                            if(stepsX[j][i]!=0 || stepsY[j][i]!=0) {
                                previewMorphGrid.getControlPoints()[j][i].setXY((int) (previewMorphGrid.getControlPoints()[j][i].getX() + stepsX[j][i]), (int) (previewMorphGrid.getControlPoints()[j][i].getY() + stepsY[j][i]));
                                previewMorphGrid.updateTrianglePreview(j, i, previewMorphGrid.getControlPoints()[j][i]);
                            }
                        }
                    }
                }
            }
        });

        morphView.getPreviewMorphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);
                previewTimer.stop();
                makePreviewFrame();
                calcSteps(5, previewMorphGrid);

                previewMorphButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        animatePreview();
                    }
                });
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
        frameCount = 0;
        previewFrame.getContentPane().remove(previewMorphGrid);
        previewMorphGrid = new MorphGrid(MorphController.this.morphGridBefore);
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
