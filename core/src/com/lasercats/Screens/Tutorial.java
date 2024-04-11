package com.lasercats.Screens;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class Tutorial {
    private TextButton tutorialButton;
    private Image tutorialImage;
    private Label tutorialDescription;

    public Tutorial(Skin skin) {
        tutorialButton = new TextButton("", skin);
        tutorialImage = new Image();
        tutorialDescription = new Label("", skin);
    }
    public void setTutorialName(String name) {
        tutorialButton.setText(name);
    }
    public void setTutorialImage(Drawable image) {
        tutorialImage.setDrawable(image);
    }
    public void setTutorialDescription(String description) {
        tutorialDescription.setText(description);
    }
    public TextButton getTutorialButton() {
        return tutorialButton;
    }
    public Label getTutorialDescription() {
        return tutorialDescription;
    }
    public  Image getTutorialImage() {
        return tutorialImage;
    }
}
