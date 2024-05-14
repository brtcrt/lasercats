package com.lasercats.GameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class CatLaser extends Laser {

    private Player player;
    public CatLaser(Player player, Viewport viewport, ArrayList<PhysicsObject> physicsObjects)
    {
        super(player.getX(), player.getY(),new Vector2(1,1), viewport, physicsObjects);
        this.player = player;
        addObjectToIgnore(player, true);
        player.addLaser(this);
    }
    @Override
    public void process() {
        x1 = player.x + player.width - player.width/12;
        y1 = player.y + player.height + player.height;
        if (player.direction.x < 0)
        {
            x1 = player.x + player.width/12;
        }
        super.process();
    }
}
