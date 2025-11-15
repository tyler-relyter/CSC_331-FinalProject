package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Player {
    //all the stuff the player can have as properties.
    private final float width;
    private final float height;
    private int health;
    private final Vector2 position;
    private final Vector2 velocity;
    private final float speed;

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
    private boolean isWalking;
    private boolean isAttacking;

    // animation texture arrays
    private final Array<Texture> idleAnimationTextures;
    private final Array<Texture> walkAnimationTextures;
    private final Array<Texture> attackAnimationTextures;

    public Player(float x, float y){
        // sets all the initial values when you create the player object
        this.position = new Vector2(x,y);
        this.velocity = new Vector2(0, 0); // this is for movement in libGDX to set the rate of player movement.
        this.speed = 100f;
        this.stateTime = 0f; //this is the amount of time an animation has been playing.
        this.direction = "down";
        this.isWalking = false;
        this.isAttacking = false;
        //Set player size
        this.width = 15;
        this.height = 15;
        //animation vars
        this.idleAnimationTextures = new Array<>(); //this holds every created texture for the animations.
        this.walkAnimationTextures = new Array<>();
        this.attackAnimationTextures = new Array<>();
        loadAnimations(); //this loads all player animations into memory.
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

        //Idle facing up
        Array<TextureRegion> idleUpFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleUp/idle_up_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);
            idleUpFrames.add(new TextureRegion((frameTexture)));
        }
        idleUpAnimation = new Animation<>(0.1f, idleUpFrames, Animation.PlayMode.LOOP);

        //Idle facing left
        Array<TextureRegion> idleLeftFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleLeft/idle_left_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);
            idleLeftFrames.add(new TextureRegion(frameTexture));
        }
        idleLeftAnimation = new Animation<>(0.1f, idleLeftFrames, Animation.PlayMode.LOOP);

        //idle facing right
        Array<TextureRegion> idleRightFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleRight/idle_right_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);
            idleRightFrames.add(new TextureRegion(frameTexture));
        }
        idleRightAnimation = new Animation<>(0.1f, idleRightFrames, Animation.PlayMode.LOOP);

        //walk downward animations
        Array<TextureRegion> walkDownFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkDown/walk_down_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkDownFrames.add(new TextureRegion(frameTexture));
        }
        walkDownAnimation = new Animation<>(0.1f, walkDownFrames, Animation.PlayMode.LOOP);

        //walk upward animations
        Array<TextureRegion> walkUpFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkUp/walk_up_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkUpFrames.add(new TextureRegion(frameTexture));
        }
        walkUpAnimation = new Animation<>(0.1f, walkUpFrames, Animation.PlayMode.LOOP);

        //walk left animation
        Array<TextureRegion> walkLeftFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkLeft/walk_left_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkLeftFrames.add(new TextureRegion(frameTexture));
        }
        walkLeftAnimation = new Animation<>(0.1f, walkLeftFrames, Animation.PlayMode.LOOP);

        // walk right animation
        Array<TextureRegion> walkRightFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkRight/walk_right_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkRightFrames.add(new TextureRegion(frameTexture));
        }
        walkRightAnimation = new Animation<>(0.1f, walkRightFrames, Animation.PlayMode.LOOP);

        //Attack up animation
        Array<TextureRegion> attackUpFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackUp/attack_up_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackUpFrames.add(new TextureRegion(frameTexture));
        }
        attackUpAnimation = new Animation<>(0.1f, attackUpFrames, Animation.PlayMode.LOOP);

        //Attack down animation
        Array<TextureRegion> attackDownFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackDown/attack_down_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackDownFrames.add(new TextureRegion(frameTexture));
        }
        attackDownAnimation = new Animation<>(0.1f, attackDownFrames, Animation.PlayMode.LOOP);

        // Attack left animation
        Array<TextureRegion> attackLeftFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackLeft/attack_left_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackLeftFrames.add(new TextureRegion(frameTexture));
        }
        attackLeftAnimation = new Animation<>(0.1f, attackLeftFrames, Animation.PlayMode.LOOP);

        //Attack right animation
        Array<TextureRegion> attackRightFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackRight/attack_right_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackRightFrames.add(new TextureRegion(frameTexture));
        }
        attackRightAnimation = new Animation<>(0.1f, attackRightFrames, Animation.PlayMode.LOOP);

    }

    public void draw(SpriteBatch spriteBatch){
        Animation<TextureRegion> currentAnimation = idleDownAnimation; // default animation when game loads.

        // decide which animation to use
        if (!isWalking && !isAttacking) { // if the character is not moving:
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
        } else if (isWalking && !isAttacking){
            if (direction.equals("down")){
                currentAnimation = walkDownAnimation;
            }
            if (direction.equals("up")) {
                currentAnimation = walkUpAnimation;
            }
            if (direction.equals("left")){
                currentAnimation = walkLeftAnimation;
            }
            if (direction.equals("right")){
                currentAnimation = walkRightAnimation;
            }
        } else if (!isWalking && isAttacking){
            if (direction.equals("down")){
                currentAnimation = attackDownAnimation;
            }
            if (direction.equals("up")) {
                currentAnimation = attackUpAnimation;
            }
            if (direction.equals("left")){
                currentAnimation = attackLeftAnimation;
            }
            if (direction.equals("right")){
                currentAnimation = attackRightAnimation;
            }
        }

        //grab the key frame from the active animation to put on the screen
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        //draw the frame to the screen when draw method is called.
        spriteBatch.draw(currentFrame, this.position.x, this.position.y, width, height);
    }

    public void update(float delta, float worldWidth, float worldHeight){
        handleInput();
        // updates position

        this.position.add(this.velocity.x * delta, this.velocity.y * delta); //adds the velocity to the players position.

        // keep player within world bounds
        this.position.x = MathUtils.clamp(this.position.x, 0, worldWidth - width);
        this.position.y = MathUtils.clamp(this.position.y, 0, worldHeight - height);

        stateTime += delta; //this adds delta time(game time) to the animation time.
        //because this is called in the main render, it continuously updates the char position (when you move him)
    }

    private void handleInput(){
        this.velocity.set(0, 0); //this sets the initial velocity to nothing until you press a key
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            this.isWalking = true;
            this.direction = "down";
            this.velocity.y -= speed; // moves character down
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            this.isWalking = true;
            this.direction = "up";
            this.velocity.y += speed; // moves character up
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            this.isWalking = true;
            this.direction = "left";
            this.velocity.x -= speed; //moves character left
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            this.isWalking = true;
            this.direction = "right";
            this.velocity.x += speed; //moves character right
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            this.isWalking = false;
            this.isAttacking = true;
            //implement attack animation based on direction facing.
        }
        else {
            this.isWalking = false;
            this.isAttacking = false;
            this.velocity.x = 0;
            this.velocity.y = 0;
        }
//        if (!Gdx.input.isKeyPressed(Input.Keys.SPACE)){
//            isWalking = false;
//            this.isAttacking = false;
//            //basically, this and the above space bar implementation say that if you hold the space bar the attack animation will play
//            //when you release it, it will stop playing and go back to idle.
//        }
    }


    public float getBottomY(){
        return this.position.y; // the bottom of the player sprite
    }

    public Rectangle getBounds(){
        return new Rectangle(this.position.x,this.position.y,width,height); // the rectangle of the player
    }

    public float getPositionX(){
        return this.position.x;
    }
    public float getPositionY(){
        return this.position.y;
    }

    public void dispose(){
        // dispose of all textures loaded by this class so it doesn't cause a memory leak.
        for (Texture texture: idleAnimationTextures){
            texture.dispose();
        }
    }
}
