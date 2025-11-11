package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    // idle animation images (textures)
    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;
    //walk animation images (textures)
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    //attack animation images(textures)
    private Animation<TextureRegion> attackDownAnimation;
    private Animation<TextureRegion> attackUpAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;

    //animation timing and direction of player
    private float stateTime;
    private String direction;
    private boolean isAttacking;

    // animation texture arrays
    private Array<Texture> idleAnimationTextures;

    public Player(float x, float y){
        // sets all the initial values when you create the player object
        this.x = x;
        this.y = y;
        this.velocity = new Vector2();// this is for movement in libGDX
        this.stateTime = 0f;
        this.direction = "down";
        this.isAttacking = false;
        this.idleAnimationTextures = new Array<>();
        loadAnimations();
    }

    private void loadAnimations() {
        //idle down animation - stores the png images into an array, then makes "TextureRegion" objects for each png
        //then with the "TextureRegion" objects, it creates the animation that's stored globally to be called by "draw" method.
        Array<TextureRegion> idleDownFrames = new Array<>(); // this is the array used to later create the textureRegion for the animation.
        for (int i = 0; i <= 5; i++){ //iterate through all the files and grab each one
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleDown/idle_down_0"+i+".png"));
            idleAnimationTextures.add(frameTexture); //add the textures to the animation array
            idleDownFrames.add(new TextureRegion(frameTexture)); // we add to the global TextureRegion array so we can create and call the animation whenever
        }

        // create the idle animation with the frames in the idleDownFrames array of textureRegion objects
        idleDownAnimation = new Animation<>(0.1f, idleDownFrames, Animation.PlayMode.LOOP);


        Array<TextureRegion> idleUpFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleUp/idle_up_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);

        }




        //Set player size
        this.width = 10;
        this.height = 10;
    }

    public void draw(SpriteBatch spriteBatch){
        Animation<TextureRegion> currentAnimation = idleDownAnimation; // default animation when game loads.

        // decide which animation to use
        if (velocity.isZero()) { // if the character is not moving:
            if (direction.equals("down")){
                currentAnimation = idleDownAnimation;
            }
            if (direction.equals("up")){
                currentAnimation = idleUpAnimation;
            }
            if (direction.equals("left")){
                currentAnimation = idleLeftAnimation;
            }
            if (direction.equals("right")){
                currentAnimation = idleRightAnimation;
            }

        } else { //if the character is moving:
            currentAnimation = walkDownAnimation;
        }



        //grab the key frame from the active animation to put on the screen
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        //draw the frame to the screen when draw method is called.
        spriteBatch.draw(currentFrame, x, y, width, height);
    }

    public void update(float delta, float worldWidth, float worldHeight){
        handleInput();
        // updates position
        x+= velocity.x * delta; // multiply it by delta so it doesn't fuck up with different fps
        y+= velocity.y * delta;

        // keep player within world bounds
        x = MathUtils.clamp(x, 0, worldWidth - width);
        y = MathUtils.clamp(y, 0, worldHeight - height);

        stateTime += delta;
        //because this is called in the main render, it continuously updates the char position (when you move him)
    }

    private void handleInput(){
        velocity.set(0, 0); //this sets the initial velocity to nothing until you press a key
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            this.direction = "down";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            this.direction = "up";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            this.direction = "left";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            this.direction = "right";
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            //implement attack animation based on direction facing.
        }
    }


    public float getBottomY(){
        return y; // the bottom of the player sprite
    }

    public Rectangle getBounds(){
        return new Rectangle(x,y,width,height); // the rectangle of the player
    }

    public void dispose(){
        // dispose of all textures loaded by this class so it doesn't cause a memory leak.
        for (Texture texture: idleAnimationTextures){
            texture.dispose();
        }
    }
}
