package com.group7; // package for project classes

// imports used by the player for input, math, rendering and textures
import com.badlogic.gdx.Gdx; // access to global Gdx methods (input, files, etc.)
import com.badlogic.gdx.Input; // keyboard constants
import com.badlogic.gdx.math.MathUtils; // clamp and other math helpers
import com.badlogic.gdx.math.Rectangle; // simple rectangle used for bounds
import com.badlogic.gdx.math.Vector2; // 2D vector for position/velocity
import com.badlogic.gdx.graphics.Texture; // texture wrapper
import com.badlogic.gdx.graphics.g2d.Animation; // animation helper
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // batch used to draw sprites
import com.badlogic.gdx.graphics.g2d.TextureRegion; // region wrapper for frames
import com.badlogic.gdx.utils.Array; // libGDX array type for frames

public class Player {
    // final world-space size for the player sprite
    private final float width; // player's width in world units
    private final float height; // player's height in world units

    // health placeholder
    private int health;

    // position and motion vectors
    private final Vector2 position; // bottom-left world position of the player
    private final Vector2 velocity; // current velocity vector (world units per second)
    private final float speed; // base speed value used when handling input

    // reference to the current map for collision queries
    private Map map;

    // animations for each state/direction
    private Animation<TextureRegion> idleDownAnimation;
    private Animation<TextureRegion> idleUpAnimation;
    private Animation<TextureRegion> idleLeftAnimation;
    private Animation<TextureRegion> idleRightAnimation;
    private Animation<TextureRegion> walkDownAnimation;
    private Animation<TextureRegion> walkUpAnimation;
    private Animation<TextureRegion> walkLeftAnimation;
    private Animation<TextureRegion> walkRightAnimation;
    private Animation<TextureRegion> attackDownAnimation;
    private Animation<TextureRegion> attackUpAnimation;
    private Animation<TextureRegion> attackLeftAnimation;
    private Animation<TextureRegion> attackRightAnimation;

    // animation timing and state flags
    private float stateTime; // accumulates delta time for animation frames
    private String direction; // "up", "down", "left", "right"
    private boolean isWalking; // walking flag
    private boolean isAttacking; // attacking flag

    // arrays storing loaded textures so they can be disposed later
    private final Array<Texture> idleAnimationTextures;
    private final Array<Texture> walkAnimationTextures;
    private final Array<Texture> attackAnimationTextures;

    // small gap to keep between player and blocked tile (world units)
    private static final float COLLISION_PADDING = 0.05f; // tweak to change visual gap

    // constructor: initialize position, vectors, sizes and load animations
    public Player(float x, float y) {
        this.position = new Vector2(x,y); // set initial position
        this.velocity = new Vector2(0, 0); // start stationary
        this.speed = 100f; // default speed (world units / second)
        this.stateTime = 0f; // start animation timer at zero
        this.direction = "down"; // default facing direction
        this.isWalking = false; // not walking initially
        this.isAttacking = false; // not attacking initially

        // set a fixed world-unit size for the player (keeps sprite visually consistent)
        this.width = 15; // width in world units
        this.height = 15; // height in world units

        // initialize arrays used to track loaded textures for later disposal
        this.idleAnimationTextures = new Array<>();
        this.walkAnimationTextures = new Array<>();
        this.attackAnimationTextures = new Array<>();

        // load sprite frames and construct animations
        loadAnimations();
    }

    // attach the Map instance used for collision detection and rendering alignment
    public void setMap(Map map) {
        this.map = map; // store reference; collision queries use this map
    }

    // loadAnimations: load textures from disk and create Animation objects
    private void loadAnimations() {
        // idle down frames
        Array<TextureRegion> idleDownFrames = new Array<>();
        for (int i = 0; i <= 5; i++){
            // load the texture file and keep a reference to dispose later
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleDown/idle_down_0"+i+".png"));
            idleAnimationTextures.add(frameTexture); // track texture
            idleDownFrames.add(new TextureRegion(frameTexture)); // add frame region
        }
        idleDownAnimation = new Animation<>(0.1f, idleDownFrames, Animation.PlayMode.LOOP); // create looping animation

        // idle up frames
        Array<TextureRegion> idleUpFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleUp/idle_up_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);
            idleUpFrames.add(new TextureRegion((frameTexture)));
        }
        idleUpAnimation = new Animation<>(0.1f, idleUpFrames, Animation.PlayMode.LOOP);

        // idle left frames
        Array<TextureRegion> idleLeftFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleLeft/idle_left_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);
            idleLeftFrames.add(new TextureRegion(frameTexture));
        }
        idleLeftAnimation = new Animation<>(0.1f, idleLeftFrames, Animation.PlayMode.LOOP);

        // idle right frames
        Array<TextureRegion> idleRightFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/IdleRight/idle_right_0"+i+".png"));
            idleAnimationTextures.add(frameTexture);
            idleRightFrames.add(new TextureRegion(frameTexture));
        }
        idleRightAnimation = new Animation<>(0.1f, idleRightFrames, Animation.PlayMode.LOOP);

        // walk down frames
        Array<TextureRegion> walkDownFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkDown/walk_down_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkDownFrames.add(new TextureRegion(frameTexture));
        }
        walkDownAnimation = new Animation<>(0.1f, walkDownFrames, Animation.PlayMode.LOOP);

        // walk up frames
        Array<TextureRegion> walkUpFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkUp/walk_up_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkUpFrames.add(new TextureRegion(frameTexture));
        }
        walkUpAnimation = new Animation<>(0.1f, walkUpFrames, Animation.PlayMode.LOOP);

        // walk left frames
        Array<TextureRegion> walkLeftFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkLeft/walk_left_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkLeftFrames.add(new TextureRegion(frameTexture));
        }
        walkLeftAnimation = new Animation<>(0.1f, walkLeftFrames, Animation.PlayMode.LOOP);

        // walk right frames
        Array<TextureRegion> walkRightFrames = new Array<>();
        for (int i = 0; i<=5; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/WalkRight/walk_right_0"+i+".png"));
            walkAnimationTextures.add(frameTexture);
            walkRightFrames.add(new TextureRegion(frameTexture));
        }
        walkRightAnimation = new Animation<>(0.1f, walkRightFrames, Animation.PlayMode.LOOP);

        // attack up frames
        Array<TextureRegion> attackUpFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackUp/attack_up_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackUpFrames.add(new TextureRegion(frameTexture));
        }
        attackUpAnimation = new Animation<>(0.1f, attackUpFrames, Animation.PlayMode.LOOP);

        // attack down frames
        Array<TextureRegion> attackDownFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackDown/attack_down_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackDownFrames.add(new TextureRegion(frameTexture));
        }
        attackDownAnimation = new Animation<>(0.1f, attackDownFrames, Animation.PlayMode.LOOP);

        // attack left frames
        Array<TextureRegion> attackLeftFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackLeft/attack_left_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackLeftFrames.add(new TextureRegion(frameTexture));
        }
        attackLeftAnimation = new Animation<>(0.1f, attackLeftFrames, Animation.PlayMode.LOOP);

        // attack right frames
        Array<TextureRegion> attackRightFrames = new Array<>();
        for (int i = 0; i <=3; i++){
            Texture frameTexture = new Texture(Gdx.files.internal("Characters/AttackRight/attack_right_0"+i+".png"));
            attackAnimationTextures.add(frameTexture);
            attackRightFrames.add(new TextureRegion(frameTexture));
        }
        attackRightAnimation = new Animation<>(0.1f, attackRightFrames, Animation.PlayMode.LOOP);
    }

    // draw the current animation frame using the supplied SpriteBatch
    public void draw(SpriteBatch spriteBatch){
        Animation<TextureRegion> currentAnimation = idleDownAnimation; // default animation

        // pick animation depending on state flags and direction
        if (!isWalking && !isAttacking) {
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

        // get the correct frame based on elapsed stateTime and draw it at position with size
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
        spriteBatch.draw(currentFrame, this.position.x, this.position.y, width, height);
    }

    // update: handle input, move, resolve collisions and clamp to world bounds
    public void update(float delta, float worldWidth, float worldHeight){
        handleInput(); // update velocity based on keyboard

        Vector2 previous = new Vector2(this.position); // copy previous position for fallback

        float dx = this.velocity.x * delta; // movement this frame in X
        float dy = this.velocity.y * delta; // movement this frame in Y

        // Move and resolve X axis first (prevents diagonal tunneling)
        this.position.x += dx; // apply horizontal movement
        if (map != null) {
            int[] blocked = findFirstBlockedTileForBounds(); // sample player bounds for blocked tiles
            if (blocked != null) {
                float visualScale = map.getVisualScale(); // tile visual size in world units
                int tx = blocked[0]; // blocked tile X index
                float tileStartX = tx * visualScale; // tile left edge world X
                float tileEndX = tileStartX + visualScale; // tile right edge world X
                if (dx > 0) {
                    // moved right into a blocked tile: place player flush left of tile minus padding
                    this.position.x = tileStartX - this.width - COLLISION_PADDING;
                } else if (dx < 0) {
                    // moved left into a blocked tile: place player flush right of tile plus padding
                    this.position.x = tileEndX + COLLISION_PADDING;
                } else {
                    // no horizontal motion: revert to previous X as safe fallback
                    this.position.x = previous.x;
                }
            }
        }

        // Move and resolve Y axis
        this.position.y += dy; // apply vertical movement
        if (map != null) {
            int[] blocked = findFirstBlockedTileForBounds(); // check vertical collisions
            if (blocked != null) {
                float visualScale = map.getVisualScale(); // tile visual size in world units
                int ty = blocked[1]; // blocked tile Y index
                float tileStartY = ty * visualScale; // tile bottom edge world Y
                float tileEndY = tileStartY + visualScale; // tile top edge world Y
                if (dy > 0) {
                    // moved up into a blocked tile: place player just below tile
                    this.position.y = tileStartY - this.height - COLLISION_PADDING;
                } else if (dy < 0) {
                    // moved down into a blocked tile: place player just above tile
                    this.position.y = tileEndY + COLLISION_PADDING;
                } else {
                    // no vertical motion: revert to previous Y as safe fallback
                    this.position.y = previous.y;
                }
            }
        }

        // clamp position so player stays inside world rectangle
        this.position.x = MathUtils.clamp(this.position.x, 0, worldWidth - width);
        this.position.y = MathUtils.clamp(this.position.y, 0, worldHeight - height);

        // advance animation time
        stateTime += delta;
    }

    // sample points on the player's bounds and return the first blocked tile found (tile coords)
    private int[] findFirstBlockedTileForBounds() {
        if (map == null) return null; // no map -> no collisions

        // sample the four corners and the center of the player's bounding box
        float left = this.position.x; // left edge
        float right = this.position.x + this.width; // right edge
        float bottom = this.position.y; // bottom edge
        float top = this.position.y + this.height; // top edge
        float centerX = this.position.x + this.width * 0.5f; // center X
        float centerY = this.position.y + this.height * 0.5f; // center Y

        // samples stored as interleaved x,y pairs
        float[] samples = new float[] {
            left, bottom,
            right, bottom,
            left, top,
            right, top,
            centerX, centerY
        };

        // iterate over samples and query the map for blocking
        for (int i = 0; i < samples.length; i += 2) {
            float sx = samples[i]; // sample X
            float sy = samples[i+1]; // sample Y
            try {
                if (map.isBlocked(sx, sy)) { // map treats coordinates in world units
                    int tileX = (int) Math.floor(sx / map.getVisualScale()); // compute tile index X
                    int tileY = (int) Math.floor(sy / map.getVisualScale()); // compute tile index Y
                    return new int[]{tileX, tileY}; // return first blocked tile found
                }
            } catch (Exception e) {
                // if map check fails, still return the tile indices for the sample
                int tileX = (int) Math.floor(sx / map.getVisualScale());
                int tileY = (int) Math.floor(sy / map.getVisualScale());
                return new int[]{tileX, tileY};
            }
        }
        return null; // nothing blocked in sampled points
    }

    // update velocity based on keyboard input; sets state flags and direction accordingly
    private void handleInput(){
        this.velocity.set(0, 0); // default to no movement each frame

        // read directional keys and set velocity along axis
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)){
            this.isWalking = true;
            this.direction = "down";
            this.velocity.y -= speed; // move down
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)){
            this.isWalking = true;
            this.direction = "up";
            this.velocity.y += speed; // move up
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            this.isWalking = true;
            this.direction = "left";
            this.velocity.x -= speed; // move left
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            this.isWalking = true;
            this.direction = "right";
            this.velocity.x += speed; // move right
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            // attack action (stops walking)
            this.isWalking = false;
            this.isAttacking = true;
        }
        else {
            // no relevant key pressed: idle
            this.isWalking = false;
            this.isAttacking = false;
            this.velocity.x = 0;
            this.velocity.y = 0;
        }
    }

    // accessor used by camera to follow the player
    public float getBottomY(){
        return this.position.y; // bottom Y of player
    }

    // returns a Rectangle representing the player's bounds in world coordinates
    public Rectangle getBounds(){
        return new Rectangle(this.position.x,this.position.y,width,height);
    }

    // position getters
    public float getPositionX(){
        return this.position.x;
    }
    public float getPositionY(){
        return this.position.y;
    }

    // dispose loaded textures to free GPU memory
    public void dispose(){
        for (Texture texture: idleAnimationTextures){
            texture.dispose(); // dispose each idle texture
        }
        // walk and attack arrays were allocated but not tracked for disposal in this file;
        // if textures were added to them, dispose them here (kept minimal to match original).
    }
}
