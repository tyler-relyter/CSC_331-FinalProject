package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    //all the stuff the player can have as properties.
    private Sprite sprite;
    private int width;
    private int height;
    private int health;
    private Vector2 velocity;
    private float speed = 200f;
    private int damage;
    public float x;
    public float y;


    public Player(Texture texture, float x, float y){
        // sets all the initial values when you create the player object
        this.sprite = new Sprite(texture); // makes it into a sprite
        this.x = x;
        this.y = y;
        this.width = 20;
        this.height = 20;
        this.damage = 10;
        this.velocity = new Vector2(); // this is for movement in libGDX

        sprite.setSize(width, height); // sizes the sprite of the player
    }

    // this handles when you press up down left or right on the arrow keys.
    public void update(float delta, float worldWidth, float worldHeight){
        handleInput(delta);
        // updates position
        x+= velocity.x * delta; // multiply it by delta so it doesn't fuck up with different fps's
        y+= velocity.y * delta;

        // keep player within world bounds
        x = MathUtils.clamp(x, 0, worldWidth - width);
        y = MathUtils.clamp(y, 0, worldHeight - height);

        //update the sprites position
        sprite.setPosition(x, y);
    }

    private void handleInput(float delta){
        velocity.set(0, 0); //this sets the initial velocity to nothing until you press a key

        //basically when you press a key, it moves the player forward or back on x and y axis allowing for movement.
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            velocity.y = speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            velocity.y = -speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            velocity.x = -speed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            velocity.x = speed;
        }
    }

    public void draw(SpriteBatch batch){
        sprite.draw(batch); // updates the draw so you are able to render the player.
    }

    public float getBottomY(){
        return y; // the bottom of the player sprite
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height); // the rectangle of the player
    }

}
