package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.LobbyScreen;

public class LevelLoader extends Level {

    private static final int FINAL_LEVEL_COUNT = 5;
    private int levelCount;

    public LevelLoader(Game game, Client client, LobbyScreen lobbyScreen, int levelCount) {
        super(game, client, lobbyScreen);
        this.levelCount = levelCount;
        LevelEditor.loadFromFile("levels/" + levelCount + ".json", gameObjects, physicsObjects);
        exitGate = findExitGate();
        super.setPlayerStarts();
    }
    @Override
    protected void displayLevelEnding() {
        if (isGameOver()) {
            super.displayLevelEnding();
            if (levelCount >= FINAL_LEVEL_COUNT) {
                game.setScreen(lobby);
            } else {
                game.setScreen(new LevelLoader(game, client, lobby, this.levelCount + 1));
            }
        }
    }
}