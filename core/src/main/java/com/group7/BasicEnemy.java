package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BasicEnemy extends Enemy {
    // creates a basic enemy that is scattered around the map
    public BasicEnemy(float x, float y, Player target, String path) {
        super(x, y, target);
        this.texture = new Texture(Gdx.files.internal(path));
    }

    @Override
    protected void updateAI(float delta) {
        // no movement for now
        velocity.set(0, 0);
    }
}
