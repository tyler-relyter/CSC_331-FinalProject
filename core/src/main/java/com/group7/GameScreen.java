package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Array;

/**
 GameScreen
 - Implements the main gameplay screen using ScreenAdapter to simplify lifecycle.
 - Responsible for loading the map, creating player and enemies, updating
   game logic each frame, and rendering world + entities.
 - Keep heavy initialization in show() and release resources in dispose().
*/
public class GameScreen extends ScreenAdapter {
    // Reference to the parent Game (MainEntry) so the screen can request
    // transitions or use common services.
    private final MainEntry game;

    // Camera & viewport define how the world is projected to the screen.
    private OrthographicCamera camera;
    private Viewport viewport;

    // Path to map asset and the runtime map instance.
    private static final String MAP_PATH = "Maps/survival2d_knockoff.tmx";
    private Map gameMap;

    // Virtual viewport size expressed in world units. These control the
    // camera's orthographic projection width/height.
    private static final float VIEW_WIDTH = 100f;
    private static final float VIEW_HEIGHT = 100f;

    // World dimensions in world units (derived from map tile size * visual scale).
    private float worldWidth;
    private float worldHeight;

    // Player and a sample enemy instance.
    private Player player;
    private BasicEnemy enemy;

    // Container for additional entities that should be updated / rendered.
    private Array<GameEntity> entities;

    // Game logic helper for damage interactions (keeps damage rules separate).
    private DamageLogic gameDamageLogic;

    // Example state flag; when true some gates were unlocked and should not be
    // unlocked again.
    private boolean gatesUnlocked = false;

    // Rendering helpers: batch for sprites and font for debug text.
    private SpriteBatch spriteBatch;
    private BitmapFont font;

    // Constructor accepts the parent Game to allow screen switching later.
    public GameScreen(MainEntry game) {
        this.game = game;
    }

    /** show()
     - Called once when this screen becomes the current screen for the Game.
     - Perform heavy initialization here (loading map, creating entities, etc).
    */
    @Override
    public void show() {
        // Visual scale used to convert tile units into world units for rendering.
        float mapVisualScale = 6f;

        // Create an orthographic camera; helpful for 2D games.
        camera = new OrthographicCamera();
        // Set camera to orthographic projection with a fixed world view size.
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);

        // Use an ExtendViewport so aspect ratio changes extend the world bounds
        // while keeping scale consistent.
        viewport = new ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);

        // Create a single SpriteBatch for all sprite rendering (efficient).
        spriteBatch = new SpriteBatch();

        // Simple BitmapFont for debug or UI text; default font used for brevity.
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        // Load the map. The Map class is expected to wrap Tiled map loading,
        // provide rendering and collision helpers.
        gameMap = new Map(MAP_PATH, mapVisualScale);

        // Compute world dimensions from tile counts provided by gameMap and scale.
        worldWidth = gameMap.getWorldWidthTiles() * mapVisualScale;
        worldHeight = gameMap.getWorldHeightTiles() * mapVisualScale;

        // Prepare entity list and instantiate player centered in the world.
        entities = new Array<>();
        player = new Player(worldWidth / 2f, worldHeight / 2f);
        // Provide the map reference to player for collision queries, etc.
        player.setMap(gameMap);

        // Create a basic enemy positioned offset from player, give it the player
        // reference so it can chase / target the player.
        enemy = new BasicEnemy(worldWidth / 2f + 20f, worldHeight / 2f, player);
        // Provide map to enemy so it can use the same collision info.
        enemy.setMap(gameMap);

        // Add the enemy to the update/draw list.
        entities.add(enemy);

        // Initialize damage logic; keeps combat rules outside of entities themselves.
        gameDamageLogic = new DamageLogic(player, enemy);
    }

    /**
     render()
     - Called every frame with delta time (seconds since last frame).
     - Update game state first, then render using camera and SpriteBatch.
    */
    @Override
    public void render(float delta) {
        // Update damage resolution logic (checks attacks, health, etc).
        gameDamageLogic.update();

        // Example game progression: when player has killed one enemy, unlock a gate.
        if (!gatesUnlocked && player.getKillCount() == 1) {
            gameMap.unlockBossGate();
            gatesUnlocked = true;
        }

        // Update each entity (movement, AI, animations) with world bounds for clamping.
        for (GameEntity entity : entities) {
            entity.update(delta, worldWidth, worldHeight);
        }

        // Clear screen to black before drawing new frame.
        ScreenUtils.clear(Color.BLACK);

        // Apply viewport in case window size changed; this also updates GL viewport.
        viewport.apply();

        // Update camera position to follow the player and then update camera matrices.
        updateCamera();
        camera.update();

        // Render the Tiled map using the camera so map tiles are in correct view.
        gameMap.render(camera);

        // Update player (movement, animations, input handling) after map render if needed.
        player.update(delta, worldWidth, worldHeight);

        // Prepare sprite batch with camera combined matrix to render sprites in world coords.
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        // Draw the player sprite.
        player.draw(spriteBatch);
        // Draw all living entities from the entities array.
        for (GameEntity entity : entities) {
            if (entity.getIsAlive()) {
                entity.draw(spriteBatch);
            }
        }
        spriteBatch.end();

        // Debug: draw the player's attack rectangle using ShapeRenderer.
        // Note: allocating a ShapeRenderer per frame should be removed after testing as this is a severe memory leak.
        ShapeRenderer tempRect = new ShapeRenderer();
        tempRect.begin(ShapeRenderer.ShapeType.Line);
        tempRect.setColor(Color.WHITE);
        tempRect.setProjectionMatrix(camera.combined);
        // Get player's attack bounds and draw the rectangle.
        float x = player.playerAttackBounds.x;
        float y = player.playerAttackBounds.y;
        float w = player.playerAttackBounds.width;
        float h = player.playerAttackBounds.height;
        tempRect.rect(x, y, w, h);
        tempRect.end();
        tempRect.dispose();
    }

    /**
     * updateCamera()
     - Update camera position to follow the player with a slight offset.
     - This keeps the player near the center of the view.
     - Adjust offsets as needed for better framing.
     */
    private void updateCamera() {
        camera.position.x = player.getPositionX() + 5f;
        camera.position.y = player.getPositionY() + 5f;
    }

    /**
     resize()
     - Called when the window is resized. Update viewport to adapt rendering.
    */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    /**
     dispose()
     - Release resources when the screen is destroyed to avoid memory leaks.
     - Check for null before disposing because some resources may not have been created.
    */
    @Override
    public void dispose() {
        if (spriteBatch != null) spriteBatch.dispose();
        if (font != null) font.dispose();
        if (player != null) player.dispose();
        if (enemy != null) enemy.dispose();
        if (gameMap != null) gameMap.dispose();
    }

}
