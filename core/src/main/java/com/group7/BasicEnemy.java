package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BasicEnemy extends Enemy {

    public BasicEnemy(float x, float y, Player target) {
        super(x, y, target);
        this.texture = new Texture(Gdx.files.internal("Enemys/bluefire.png"));
    }

    @Override
    protected void updateAI(float delta) {
        // no movement for now
        velocity.set(0, 0);
    }
}
