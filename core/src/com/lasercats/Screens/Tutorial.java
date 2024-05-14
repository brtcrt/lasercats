package com.lasercats.Screens;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class Tutorial {
    private TextButton tutorialButton;
    private Image tutorialImage;
    private Label tutorialDescription;
    private final static float FONT_SCALING = 2;

    public Tutorial(Skin skin) {
        tutorialButton = new TextButton("", skin);
        tutorialDescription = new Label("", skin);
        tutorialDescription.setFontScale(FONT_SCALING);
        tutorialDescription.setWrap(true);
        tutorialDescription.setAlignment(Align.center);
    }
    public void setTutorialName(String name) {
        tutorialButton.setText(name);
    }
    public void setTutorialImage(Image image) {
        tutorialImage = image;
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