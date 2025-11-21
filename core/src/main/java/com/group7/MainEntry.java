package com.group7;

// imports for libGDX application structure, rendering, camera and viewport
import com.badlogic.gdx.Game; // base class for libGDX game apps
import com.badlogic.gdx.Gdx; // access to global Gdx functions (delta time, etc.)
import com.badlogic.gdx.graphics.Color; // color constants
import com.badlogic.gdx.graphics.OrthographicCamera; // camera used to render world
import com.badlogic.gdx.graphics.g2d.BitmapFont; // simple font for UI/debug text
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // main batch for drawing sprites
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils; // screen clearing helpers
import com.badlogic.gdx.utils.viewport.*; // viewport types used to handle resizing
import com.badlogic.gdx.utils.Array;


public class MainEntry extends Game {
    private OrthographicCamera camera; // camera used for map and sprite rendering
    private Viewport viewport; // viewport used to maintain aspect ratio

    // path to TMX map resource in assets folder
    private static final String MAP_PATH = "Maps/survival2d_knockoff.tmx";
    private Map gameMap; // Map instance for rendering and collision queries

    // desired view size in world units for the camera
    private static final float VIEW_WIDTH = 100f;
    private static final float VIEW_HEIGHT = 100f;

    private float worldWidth; // world width in world units (tiles * visualScale)
    private float worldHeight; // world height in world units

    private Player player; // player instance
    private BasicEnemy enemy; // Enemy instance
    private Array<GameEntity> entities;

    private DamageLogic gameDamageLogic;

    private static SpriteBatch spriteBatch; // shared sprite batch for drawing sprites
    private BitmapFont font; // font for debugging or HUD text

    @Override
    public void create() {
        // visual scale: 1f = normal tile size from TMX, >1 makes tiles larger visually
        float mapVisualScale = 6f; // chosen to make tiles visually larger

        camera = new OrthographicCamera(); // create world camera
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT); // set camera projection and viewport size

        viewport = new ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT, camera); // create an extend viewport

        spriteBatch = new SpriteBatch(); // create main sprite batch
        font = new BitmapFont(); // instantiate default font
        font.setColor(Color.WHITE); // set font color to white

        // create the map with the selected visual scale; Map will load TMX and create renderer
        gameMap = new Map(MAP_PATH, mapVisualScale); // pass visual scale to Map constructor

        // compute world dimensions in world units so camera and clamping can use them
        worldWidth = gameMap.getWorldWidthTiles() * mapVisualScale; // tiles * visual scale => world units
        worldHeight = gameMap.getWorldHeightTiles() * mapVisualScale; // same for height

        entities = new Array<>();
        // create player centered in the world initially
        player = new Player(worldWidth / 2f, worldHeight / 2f);
        player.setMap(gameMap); // attach map to player so collisions work

        enemy = new BasicEnemy(worldWidth / 2f + 20f, worldHeight / 2f, player);
        enemy.setMap(gameMap);

        entities.add(enemy);

        gameDamageLogic = new DamageLogic(player, enemy);
    }

    @Override
    public void render(){
        float delta = Gdx.graphics.getDeltaTime(); // compute elapsed time since last frame

        gameDamageLogic.update();

        for (GameEntity entity : entities) {
            entity.update(delta, worldWidth, worldHeight);
        }

        ScreenUtils.clear(Color.BLACK); // clear screen to black
        viewport.apply(); // apply viewport transforms to gl viewport
        updateCamera(); // update camera position based on player
        camera.update(); // update camera matrices

        // Render tiled map first so sprites appear above it
        gameMap.render(camera); // mapRenderer draws the map using provided camera

        player.update(delta, worldWidth, worldHeight);
        // Then render player and other sprites using the shared SpriteBatch
        spriteBatch.setProjectionMatrix(camera.combined); // align batch with camera
        spriteBatch.begin(); // begin drawing sprites
        player.draw(spriteBatch); //draws the player

        for (GameEntity entity : entities) { // draw entities if alive, else stop drawing.
            if (entity.getIsAlive()){
                entity.draw(spriteBatch);
            }
        }
        spriteBatch.end(); // finish sprite drawing

        //update enemies here, if we had any :/ *insert timmy turners dad in front of a trophy case meme here*




        //render a temporary rectangle object to show the players attack range for testing
        ShapeRenderer tempRect = new ShapeRenderer();
        tempRect.begin(ShapeRenderer.ShapeType.Line);
        tempRect.setColor(Color.WHITE);
        tempRect.setProjectionMatrix(camera.combined);
        float x = player.playerAttackBounds.x;
        float y = player.playerAttackBounds.y;
        float w = player.playerAttackBounds.width;
        float h = player.playerAttackBounds.height;

        tempRect.rect(x, y, w, h);
        tempRect.end();


    }

    // camera follows player with a small offset to keep player inside viewport center-ish
    private void updateCamera(){
        camera.position.x = player.getPositionX() + 5f; // offset horizontally
        camera.position.y = player.getPositionY() + 5f; // offset vertically
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height); // forward resize to the viewport
    }


    @Override
    public void dispose(){
        spriteBatch.dispose(); // free sprite batch GPU resources
        font.dispose(); // dispose font
        if (player != null) player.dispose(); // dispose player resources (textures)
        if (enemy != null) enemy.dispose(); // dispose of enemey resources
        if (gameMap != null) gameMap.dispose(); // dispose map and renderer
    }
}
