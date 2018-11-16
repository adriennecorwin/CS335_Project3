package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FrameBuilder extends JFrame{
    private final static int windowWidth = 1500;
    private final static int windowHeight = 1000;
    public JFrame morphFrame;

    public FrameBuilder() {
        JFrame morphFrame = new JFrame("Morph");
//        morphFrame.getContentPane().setLayout(new FlowLayout());
        MorphGrid morphGridBefore = new MorphGrid(10);
        MorphGrid morphGridAfter = new MorphGrid(10);
        MorphView morphView = new MorphView();
        JPanel morphGrids = new JPanel();
        JLabel windowLabel = new JLabel("Morpheus");
        windowLabel.setFont (windowLabel.getFont ().deriveFont (64.0f));
        windowLabel.setHorizontalAlignment(JLabel.CENTER);
        morphGrids.add(morphGridBefore, BorderLayout.WEST);
        morphGrids.add(morphGridAfter, BorderLayout.EAST);
        MorphController morphController = new MorphController(morphGridBefore, morphGridAfter, morphView);
        morphFrame.getContentPane().add(windowLabel, BorderLayout.NORTH);
        morphFrame.getContentPane().add(morphGrids, BorderLayout.CENTER);
//        morphFrame.getContentPane().add(morphGridBefore, BorderLayout.WEST);
//        morphFrame.getContentPane().add(morphGridAfter, BorderLayout.EAST);
        morphFrame.getContentPane().add(morphView, BorderLayout.SOUTH);
        morphFrame.setSize(windowWidth, windowHeight);
        morphFrame.setVisible(true);
        morphFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public JFrame getMorphFrame() {
        return morphFrame;
    }
}
