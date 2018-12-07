package com.company;


import javax.swing.*;
import java.awt.*;

public class MorphView extends JPanel{
    private JButton previewMorphButton;
    private JButton resetButton;
    private JLabel morphTimeLabel;
    private JLabel morphFrameLabel;
    private JSlider morphTimeSlider;
    private JSlider gridResSlider;
    private JLabel gridResLabel;
    private JSlider morphFrameSlider;
    private JButton morphButton;

    private JSlider inputIntensitySlider;
    private JSlider outputIntensitySlider;
    private JLabel inputIntensityLabel;
    private JLabel outputIntensityLabel;

    private JFileChooser fc;
    private JButton fileOpenInput;
    private JButton fileOpenOutput;

    public MorphView(){
        setLayout(new GridLayout(3, 5));
//        previewMorphButton = new JButton("Preview Morph");
        gridResSlider = new JSlider(5,20,10); //controls duration of morph
        gridResLabel = new JLabel("Grid Resolution: "+gridResSlider.getValue()+"x"+gridResSlider.getValue());
        gridResSlider.setMajorTickSpacing(5);
        gridResSlider.setSnapToTicks(true);
        gridResSlider.getPaintTicks();
        previewMorphButton = new JButton("Preview Morph"); //opens preview window
        resetButton = new JButton("Reset"); //puts control points back to original positions
        morphTimeSlider = new JSlider(1,5,2); //controls duration of morph
        morphFrameSlider = new JSlider(5, 60, 10); //controls number of tween frames
        morphTimeLabel = new JLabel("Morph Duration: "+morphTimeSlider.getValue()+" seconds");
        morphFrameLabel = new JLabel("Number of Tween Frames: "+morphFrameSlider.getValue());
        morphButton = new JButton("Morph");
        inputIntensitySlider = new JSlider(1, 200, 100);
        outputIntensitySlider = new JSlider(1, 200, 100);
        inputIntensityLabel = new JLabel("Input Intensity");
        outputIntensityLabel = new JLabel("Output Intensity");
        fileOpenInput = new JButton("Set Input Image");
        fileOpenOutput = new JButton("Set Output Image");

        fc = new JFileChooser(".");

        this.add(gridResLabel);
        this.add(morphTimeLabel);
        this.add(morphFrameLabel);
        this.add(inputIntensityLabel);
        this.add(outputIntensityLabel);

        this.add(gridResSlider);
        this.add(morphTimeSlider);
        this.add(morphFrameSlider);
        this.add(inputIntensitySlider);
        this.add(outputIntensitySlider);

        this.add(fileOpenInput);
        this.add(fileOpenOutput);
        this.add(resetButton);
        this.add(previewMorphButton);
        this.add(morphButton);
    }

    //GETTERS

    public JButton getPreviewMorphButton(){
        return previewMorphButton;
    }

    public JButton getResetButton(){
        return resetButton;
    }

    public JSlider getMorphTimeSlider(){
        return morphTimeSlider;
    }

    public JSlider getMorphFrameSlider(){
        return morphFrameSlider;
    }

    public JLabel getMorphFrameLabel() {
        return morphFrameLabel;
    }

    public JLabel getMorphTimeLabel() {
        return morphTimeLabel;
    }

    public JSlider getGridResSlider(){
        return gridResSlider;
    }

    public JLabel getGridResLabel(){
        return gridResLabel;
    }

    public JButton getFileOpenInput(){
        return fileOpenInput;
    }

    public JButton getFileOpenOutput(){
        return fileOpenOutput;
    }

    public JFileChooser getFc(){
        return fc;
    }

    public JButton getMorphButton(){
        return morphButton;
    }

    public JSlider getInputIntensitySlider(){
        return inputIntensitySlider;
    }

    public JSlider getOutputIntensitySlider(){
        return outputIntensitySlider;
    }
}
