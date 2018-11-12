package com.company;

import javax.swing.*;

public class MorphView extends JPanel{
    JButton previewMorphButton;

    public MorphView(){
        previewMorphButton = new JButton("Preview Morph");
        this.add(previewMorphButton);
    }

    public JButton getPreviewMorphButton(){
        return previewMorphButton;
    }
}
