package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class BossEnemy extends Enemy {

    //Creates a boss enemy based on the enemy class.
    public BossEnemy(float x, float y, Player target) {
        super(x, y, target);
        this.width = 120f; //bigger size because its a boss.
        this.height = 60f;
        this.texture = new Texture(Gdx.files.internal("Enemys/boss.png"));
    }

    @Override
    protected void updateAI(float delta) {
        // no movement for now
        velocity.set(0, 0);
    }
}
