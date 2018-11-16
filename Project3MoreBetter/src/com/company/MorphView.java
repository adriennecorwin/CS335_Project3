package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MorphView extends JPanel{
    private JButton previewMorphButton;
    private JButton resetButton;
    private JLabel morphTimeLabel;
    private JLabel morphFrameLabel;
    private JSlider morphTimeSlider;
    private JSlider morphFrameSlider;

    JMenu file;
    private BufferedImage image;
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;
    private boolean showfiltered=false;
    private MyImageObj view;

    public MorphView(){
        previewMorphButton = new JButton("Preview Morph");
        //file = new JMenu("File");
        //this.add(file);
        buildMenus();
        this.view = new MyImageObj(readImage("snoop.jpg"));
        previewMorphButton = new JButton("Preview Morph");
        resetButton = new JButton("Reset");
        morphTimeSlider = new JSlider(1,5,2);
        morphFrameSlider = new JSlider(5, 60, 10);
        morphTimeLabel = new JLabel("Morph Duration: "+morphTimeSlider.getValue()+" seconds");
        morphFrameLabel = new JLabel("Frames Per Second: "+morphFrameSlider.getValue());

        this.add(resetButton);
        this.add(previewMorphButton);
        this.add(morphTimeLabel);
        this.add(morphTimeSlider);
        this.add(morphFrameLabel);
        this.add(morphFrameSlider);
        this.add(previewMorphButton);
    }

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

    private void buildMenus () {

        final JFileChooser fc = new JFileChooser(".");
        JMenuBar bar = new JMenuBar();
        this.add (bar);
        JMenu fileMenu = new JMenu ("File");
        JMenuItem fileopen = new JMenuItem ("Open");
        JMenuItem fileexit = new JMenuItem ("Exit");

        fileopen.addActionListener(
                new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(MorphView.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                image = ImageIO.read(file);
                            } catch (IOException e1){}

                            view.setImage(image);
                            view.showImage();
                        }
                    }
                }
        );
        fileexit.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        System.exit(0);
                    }
                }
        );

        fileMenu.add(fileopen);
        fileMenu.add(fileexit);
        bar.add(fileMenu);
    }

    public BufferedImage readImage (String file) {

        Image image = Toolkit.getDefaultToolkit().getImage(file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;
    }

}
