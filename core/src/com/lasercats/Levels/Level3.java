package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.MainMenuScreen;

public class Level3 extends Level {

    private Label gameFinishedLabel;
    public Level3(Game game, Client client, MainMenuScreen menuScreen) {
        super(game, client, menuScreen);
        LevelEditor.loadFromFile("levels/level3.json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    public void createActors() {
        levelEndDialog = new Dialog("Level Completed", skin, "c2");
		exitToMainMenuButton = new TextButton("Exit", skin, "dark");
        gameFinishedLabel = new Label("All levels complete!", skin);
    }
    @Override
    public void positionActors() {
        levelEndDialog.add(gameFinishedLabel).growX();
        levelEndDialog.add(exitToMainMenuButton).growX();
    }
}
