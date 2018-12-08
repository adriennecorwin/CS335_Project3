package com.company;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {


    //only handles square pictures and square grid
    //if not a square picture, aspect ratio will be changed
    public static void main(String[] args) {
        FrameBuilder frame = new FrameBuilder();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}
