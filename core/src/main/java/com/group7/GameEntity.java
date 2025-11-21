package com.group7;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface GameEntity {
    void update(float delta, float worldWidth, float worldHeight);
    void draw(SpriteBatch batch);
    //  TO-DO: Will need this later for player-enemy collision
    Rectangle getBounds();
    float getHealth();
    void modifyHealth(float amount);
    void dispose();
    void setIsAlive(boolean isAlive);
    boolean getIsAlive();
    void setDeathHandled(boolean isDeathHandled);
    boolean getDeathHandled();
}
