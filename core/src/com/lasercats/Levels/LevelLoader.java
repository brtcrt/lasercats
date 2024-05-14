package com.lasercats.Levels;

import com.badlogic.gdx.Game;
import com.lasercats.Client.Client;
import com.lasercats.GameObjects.Player;
import com.lasercats.Screens.LevelEditor;
import com.lasercats.Screens.LobbyScreen;

public class LevelLoader extends Level {

    private static final int FINAL_LEVEL_COUNT = 3;
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
            Player p1 = (Player) gameObjects.get(0);
            Player p2 = (Player) gameObjects.get(1);
            gameObjects.clear();
            physicsObjects.clear();
            gameObjects.add(p1);
            gameObjects.add(p2);
            physicsObjects.add(p1);
            physicsObjects.add(p2);
            client.gameObjects = gameObjects;
            client.physicsObjects = physicsObjects;
            if (levelCount >= FINAL_LEVEL_COUNT) {
                game.setScreen(lobby);
            } else {
                game.setScreen(new LevelLoader(game, client, lobby, this.levelCount + 1));
            }
        }
    }
}