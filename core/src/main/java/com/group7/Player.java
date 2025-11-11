package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Player {
    //all the stuff the player can have as properties.
    private float width;
    private float height;
    private int health;
    private final Vector2 velocity;
    public float x;
    public float y;

    // animation fields
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;
    private Animation<TextureRegion> attackAnimation;
    private float stateTime;
    private String direction;

    private Array<Texture> animationTextures;

    public Player(float x, float y){
        // sets all the initial values when you create the player object
        this.x = x;
        this.y = y;
        this.velocity = new Vector2();// this is for movement in libGDX
        this.stateTime = 0f;
        this.direction = "down";
        this.animationTextures = new Array<>();
        //loadAnimations();
    }

    private void loadAnimations() {
        //idle animation
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 0; i <= 5; i++){ //iterate through all the files and grab each one
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleDown/idle_down_0"+i+".png"));
            animationTextures.add(frameTexture); //add the textures to the animation array
            idleFrames.add(new TextureRegion(frameTexture)); // add the images as to the texture region for idle animation
        }
        idleAnimation = new Animation<>(0.1f, idleFrames, Animation.PlayMode.LOOP); // create the idle animation with the frames.

        //walk animation
        for(int i = 1; i <= )



        //Set player size
        this.width = 10;
        this.height = 10;
    }

    public void draw(SpriteBatch spriteBatch){
        Animation<TextureRegion> currentAnimation;

        // decide which animation to use
        if (velocity.isZero()) {
            currentAnimation = idleAnimation;
        } else {
            currentAnimation = walkAnimation;
        }
    }

    public void update(float delta, float worldWidth, float worldHeight){
        handleInput();
        // updates position
        x+= velocity.x * delta; // multiply it by delta so it doesn't fuck up with different fps's
        y+= velocity.y * delta;

        // keep player within world bounds
        x = MathUtils.clamp(x, 0, worldWidth - width);
        y = MathUtils.clamp(y, 0, worldHeight - height);

        stateTime += delta;
        //because this is called in the main render, it continuously updates the char position (when you move him)
    }

    private void handleInput(){
        velocity.set(0, 0); //this sets the initial velocity to nothing until you press a key
    }


    public float getBottomY(){
        return y; // the bottom of the player sprite
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height); // the rectangle of the player
    }

}
