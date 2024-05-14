package com.lasercats.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.lasercats.Screens.MainMenuScreen;

public class LaserCats extends Game {
    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }
    @Override
    public void render() {
        Gdx.gl20.glClearColor(0, 0, 0, 1);
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        super.render();
    }
}
