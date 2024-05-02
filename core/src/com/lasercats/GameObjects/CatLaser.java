package com.lasercats.GameObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class CatLaser extends Laser{
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

        vertices.get(0).set(new Vector2(player.x + player.width - player.width/12,
                    player.y + player.height + player.height));
        if (player.direction.x < 0)
        {
            vertices.get(0).set(new Vector2(player.x + player.width/12,
                    vertices.get(0).y));
        }

        super.process();
    }

}
