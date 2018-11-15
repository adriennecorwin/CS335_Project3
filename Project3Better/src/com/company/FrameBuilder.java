package com.company;

import javax.swing.*;
import java.awt.*;

public class FrameBuilder extends JFrame{
    private final static int windowWidth = 1500;
    private final static int windowHeight = 1000;
    public JFrame morphFrame;

    public FrameBuilder() {
        morphFrame = new JFrame("Morph");
        morphFrame.getContentPane().setLayout(new FlowLayout());
        MorphGrid morphGridBefore = new MorphGrid(10);
        MorphGrid morphGridAfter = new MorphGrid(10);
        MorphView morphView = new MorphView();
        MorphController morphController = new MorphController(morphGridBefore, morphGridAfter, morphView);
        morphFrame.getContentPane().add(morphGridBefore);
        morphFrame.getContentPane().add(morphGridAfter);
        morphFrame.getContentPane().add(morphView);
        morphFrame.setSize(windowWidth, windowHeight);
        morphFrame.setVisible(true);
    }

    public JFrame getMorphFrame() {
        return morphFrame;
    }
}
