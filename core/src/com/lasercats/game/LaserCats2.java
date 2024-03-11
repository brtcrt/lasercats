package com.lasercats.game;

import com.badlogic.gdx.Game;
import com.lasercats.Screens.MainMenuScreen;

public class LaserCats2 extends Game {
    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }
}
