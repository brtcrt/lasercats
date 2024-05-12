package com.lasercats.Game;

import com.badlogic.gdx.Game;
import com.lasercats.Screens.MainMenuScreen;

public class LaserCats extends Game {
    @Override
    public void create() {
        this.setScreen(new MainMenuScreen(this));
    }
}
