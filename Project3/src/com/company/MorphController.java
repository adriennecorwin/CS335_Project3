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
                if(isDragging){
                    if(e.getX()>605 && e.getY()<605 & e.getYOnScreen()>morphGridAfter.getLocation().getY()){
                        morphGridAfter.updateTrianlges(605, e.getY());
                    }
                    else if(e.getY()>605 && e.getX()<605 & e.getXOnScreen()>morphGridAfter.getLocation().getX()) {
                        morphGridAfter.updateTrianlges(e.getX(), 605);
                    }
                    else if(e.getX()>605 && e.getY()>605){
                        morphGridAfter.updateTrianlges(605, 605);
                    }
                    else if(e.getXOnScreen()<morphGridAfter.getLocation().getX() && e.getY()<605 & e.getYOnScreen()>morphGridAfter.getLocation().getY()){
                        morphGridAfter.updateTrianlges(0, e.getY());
                    }
                    else if(e.getYOnScreen()<morphGridAfter.getLocation().getY() && e.getX()<605 & e.getXOnScreen()>morphGridAfter.getLocation().getX()){
                        morphGridAfter.updateTrianlges(e.getX(), 0);
                    }
                    else if (e.getXOnScreen()<morphGridAfter.getLocation().getX() && e.getYOnScreen()<morphGridAfter.getLocation().getY()){
                        morphGridAfter.updateTrianlges(0, 0);
                    }
                    else{
                        morphGridAfter.updateTrianlges(e.getX(), e.getY());
                    }
                }
            }
        });


        morphView.getPreviewMorphButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previewMorphGrid = new MorphGrid(morphGridBefore);
                Timer previewTimer = new Timer(100, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        int distX;
                        int distY;
                        int stepX;
                        int stepY;
                        for(int i=0; i<morphGridBefore.getGridDim()-1; i++){
                            for(int j=0; j<morphGridBefore.getGridDim()-1; j++){
                                if((morphGridAfter.getControlPoints()[j][i].getX() - previewMorphGrid.getControlPoints()[j][i].getX())>.1 || (morphGridAfter.getControlPoints()[j][i].getY() - previewMorphGrid.getControlPoints()[j][i].getY())>.1){
                                    distX = (morphGridAfter.getControlPoints()[j][i].getX()-previewMorphGrid.getControlPoints()[j][i].getX());
                                    distY = (morphGridAfter.getControlPoints()[j][i].getY()-previewMorphGrid.getControlPoints()[j][i].getY());
                                    double angle = Math.atan2(distY, distX);

                                    stepX = Math.cos(angle)*2;
                                    stepY = Math.sin(angle)*2;

                                    ControlPoint before = previewMorphGrid.getControlPoints()[j][i];

                                    previewMorphGrid.getControlPoints()[j][i].setXY((int)(previewMorphGrid.getControlPoints()[j][i].getX()+stepX),(int)(previewMorphGrid.getControlPoints()[j][i].getY()+stepY));
                                    previewMorphGrid.updateTrianglePreview(before, previewMorphGrid.getControlPoints()[j][i]);
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
        if(isDragging){
            if(e.getX()>605 && e.getY()<605 & e.getYOnScreen()>morphGridBefore.getLocation().getY()){
                morphGridBefore.updateTrianlges(605, e.getY());
            }
            else if(e.getY()>605 && e.getX()<605 & e.getXOnScreen()>morphGridBefore.getLocation().getX()) {
                morphGridBefore.updateTrianlges(e.getX(), 605);
            }
            else if(e.getX()>605 && e.getY()>605){
                morphGridBefore.updateTrianlges(605, 605);
            }
            else if(e.getXOnScreen()<morphGridBefore.getLocation().getX() && e.getY()<605 & e.getYOnScreen()>morphGridBefore.getLocation().getY()){
                morphGridBefore.updateTrianlges(0, e.getY());
            }
            else if(e.getYOnScreen()<morphGridBefore.getLocation().getY() && e.getX()<605 & e.getXOnScreen()>morphGridBefore.getLocation().getX()){
                morphGridBefore.updateTrianlges(e.getX(), 0);
            }
            else if (e.getXOnScreen()<morphGridBefore.getLocation().getX() && e.getYOnScreen()<morphGridBefore.getLocation().getY()){
                morphGridBefore.updateTrianlges(0, 0);
            }
            else{
                morphGridBefore.updateTrianlges(e.getX(), e.getY());
            }
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
