package com.lasercats.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

public abstract class LaserCatsScreen implements Screen {
    //Might not be the optimal way of doing this. Instead we can instantiate an OrthographicCamera and SpriteBatch in the Game Object that handles the screens instead of
    //one for every screen.
    OrthographicCamera camera;
    SpriteBatch batch;

    //We might need different types of viewports for different screens (or even multiple viewports for one screen). 
    //For this purpose, I think it is okay to have one viewport for every screen.
    Viewport genericViewport;
    Stage stage;
    Table root;
    Game game;
    Skin skin;
    public LaserCatsScreen(Game game) {
       this.camera = new OrthographicCamera();
       this.batch = new SpriteBatch(); 
       this.root = new Table();
       this.game = game;
    }

    //Methods inherited from the Screen interface.
	public void show() {
        this.root.setVisible(true);
    }

    public abstract void render(float delta);

    //This will no longer be used by the way, since we will never resize the window.
    public void resize(int width, int height) {
        this.genericViewport.update(width, height, true);
        this.root.setHeight(height);
        this.root.setWidth(width);
    }

    public abstract void pause(); 

    public abstract void resume(); 

    public void hide() {
        this.root.setVisible(false);
    }

    public abstract void dispose(); 

    //Methods regarding UI components.
    public abstract void createActors(); 

    public abstract void positionActors(); 

    public abstract void setListeners(); 

}
