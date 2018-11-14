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
    JButton previewMorphButton;
    JMenu file;
    private BufferedImage image;
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;
    private boolean showfiltered=false;

    public MorphView(){
        previewMorphButton = new JButton("Preview Morph");
        //file = new JMenu("File");
        //this.add(file);
        buildMenus();
        this.add(previewMorphButton);
    }

    public JButton getPreviewMorphButton(){
        return previewMorphButton;
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

                            MorphView.this.setImage(image);
                            MorphView.this.showImage();
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

    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        showfiltered=false;
        MorphView.this.repaint();
    }

    public void showImage() {
        if (bim == null) return;
        showfiltered=false;
        MorphView.this.repaint();
    }
}
