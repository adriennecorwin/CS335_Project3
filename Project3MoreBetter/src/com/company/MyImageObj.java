package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class MyImageObj extends JLabel{
    // instance variable to hold the buffered image
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;

    //  tell the paintcomponent method what to draw
    private boolean showfiltered=false;

    public MyImageObj(BufferedImage img) {
        bim = img;
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));

        this.repaint();
    }

    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        showfiltered=false;
        this.repaint();
    }

    public void showImage() {
        if (bim == null) return;
        showfiltered=false;
        this.repaint();
    }

    public void paintComponent(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        if (showfiltered)
            big.drawImage(filteredbim, 0, 0, this);
        else
            big.drawImage(bim, 0, 0, this);
    }


}
