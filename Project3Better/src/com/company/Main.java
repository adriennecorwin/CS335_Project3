package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {
    private final static int windowWidth = 1500;
    private final static int windowHeight = 1000;

    public static void main(String[] args) {
        JFrame morphFrame = new JFrame("Morph");
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
        morphFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
