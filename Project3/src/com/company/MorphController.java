package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MorphController implements MouseListener, MouseMotionListener{
    private boolean isDragging = false;
    private int pointDragged[];
    private MorphGrid morphGridBefore;
    private MorphGrid morphGridAfter;
    private MorphGrid previewMorphGrid;

    public MorphController(MorphGrid morphGridBefore, MorphGrid morphGridAfter, MorphView morphView){
        pointDragged = new int[2];
        pointDragged[0]=-1;
        pointDragged[1]=-1;
        this.morphGridBefore = morphGridBefore;
        this.morphGridAfter = morphGridAfter;
        morphGridBefore.setPointDragged(pointDragged);
        morphGridAfter.setPointDragged(pointDragged);
        morphGridBefore.addMouseListener(this);
        morphGridBefore.addMouseMotionListener(this);

        morphGridAfter.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
//                super.mousePressed(e);
                for(int i=0; i<morphGridAfter.getGridDim()-1; i++){
                    for(int j=0; j<morphGridAfter.getGridDim()-1; j++){
                        if(morphGridAfter.getControlPoints()[j][i].getShape().contains(e.getX(), e.getY())){
                            isDragging = true;
                            pointDragged = new int[2];
                            pointDragged[0] = j;
                            pointDragged[1] = i;
                            morphGridAfter.setPointDragged(pointDragged);
                            morphGridBefore.setCorrespondingPoint(pointDragged);
                            morphGridBefore.repaint();
                            break;
                        }
                    }
                }
            }
        });

        morphGridAfter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased(e);
                isDragging=false;
            }
        });

        morphGridAfter.addMouseListener(new MouseAdapter() {//if mouse exits JPANEL aka outside of bounds
            @Override
            public void mouseExited(MouseEvent e) {
//                super.mouseExited(e);

            }
        });

        morphGridAfter.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(isDragging){ //and e.getx inside panel and e.gety insdie panel
                    morphGridAfter.updateTrianlges(e.getX(), e.getY());
                }
            }
        });


        morphView.getPreviewMorphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewMorphGrid = new MorphGrid(morphGridBefore);
                previewMorphGrid.removeMouseListener(MorphController.this);
                previewMorphGrid.removeMouseMotionListener(MorphController.this);
                Timer previewTimer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int distX;
                        int distY;
                        int stepX;
                        int stepY;
                        for(int i=0; i<morphGridBefore.getGridDim()-1; i++){
                            for(int j=0; j<morphGridBefore.getGridDim()-1; j++){
                                if(morphGridAfter.getControlPoints()[j][i].getX()!= morphGridBefore.getControlPoints()[j][i].getX() || morphGridAfter.getControlPoints()[j][i].getY()!= morphGridBefore.getControlPoints()[j][i].getY()){
                                    distX = (int)(morphGridAfter.getControlPoints()[j][i].getX()-morphGridBefore.getControlPoints()[j][i].getX());
                                    distY = (int)(morphGridAfter.getControlPoints()[j][i].getY()-morphGridBefore.getControlPoints()[j][i].getY());

                                    stepX = distX/10;
                                    stepY = distY/10;
                                    previewMorphGrid.getControlPoints()[j][i].setXY((int)(previewMorphGrid.getControlPoints()[j][i].getX()+stepX),(int)(previewMorphGrid.getControlPoints()[j][i].getY()+stepY));
                                    previewMorphGrid.updateTrianglePreview(j,i);
                                }
                            }
                        }
                    }
                });

                JFrame previewFrame = new JFrame("Preview Morph");
                previewFrame.setLayout(new FlowLayout());
                JButton previewMorphButton = new JButton("Preview Morph");
                previewFrame.getContentPane().add(previewMorphButton);
                previewMorphButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        previewTimer.stop();
                        previewFrame.getContentPane().remove(previewMorphGrid);
                        previewMorphGrid = new MorphGrid(morphGridBefore);
                        previewMorphGrid.removeMouseListener(MorphController.this);
                        previewMorphGrid.removeMouseMotionListener(MorphController.this);
                        previewFrame.getContentPane().add(previewMorphGrid);
                        previewMorphGrid.repaint();
                        previewMorphGrid.revalidate();
                        previewFrame.setVisible(true);
                        previewTimer.start();
                    }
                });

                previewFrame.getContentPane().add(previewMorphGrid);
                previewFrame.setSize(700, 700);
                previewFrame.setVisible(true);
            }
        });

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        for(int i=0; i<morphGridBefore.getGridDim()-1; i++){
            for(int j=0; j<morphGridBefore.getGridDim()-1; j++){
                if(morphGridBefore.getControlPoints()[j][i].getShape().contains(e.getX(), e.getY())){
                    isDragging = true;
                    pointDragged = new int[2];
                    pointDragged[0] = j;
                    pointDragged[1] = i;
                    morphGridBefore.setPointDragged(pointDragged);
                    morphGridAfter.setCorrespondingPoint(pointDragged);
                    morphGridAfter.repaint();
                    break;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if(isDragging){ //and e.getx inside panel and e.gety insdie panel
            morphGridBefore.updateTrianlges(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        isDragging=false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
