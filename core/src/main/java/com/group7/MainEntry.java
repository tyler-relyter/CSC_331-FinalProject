package com.group7;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;

public class MainEntry extends Game {
    private OrthographicCamera camera;
    private Viewport viewport;

    private static final String MAP_PATH = "Maps/survival2d_knockoff.tmx";
    private Map gameMap;

    private static final float VIEW_WIDTH = 100f;
    private static final float VIEW_HEIGHT = 100f;

    private float worldWidth;
    private float worldHeight;

    private Player player;

    private static SpriteBatch spriteBatch;
    private BitmapFont font;

    @Override
    public void create() {
        // visual scale: 1f = normal, 2f = tiles twice as large, etc.
        float mapVisualScale = 6f;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT);

        viewport = new ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);

        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);

        gameMap = new Map(MAP_PATH, mapVisualScale); // pass desired visual scale
        worldWidth = gameMap.getWorldWidthTiles() * mapVisualScale;
        worldHeight = gameMap.getWorldHeightTiles() * mapVisualScale;

        player = new Player(worldWidth / 2f, worldHeight / 2f);
    }

    @Override
    public void render(){
        float delta = Gdx.graphics.getDeltaTime();
        player.update(delta, worldWidth, worldHeight);

        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        updateCamera();
        camera.update();

        // Render tiled map first (map renderer uses its own batch)
        gameMap.render(camera);

        // Then render game sprites using the main SpriteBatch
        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();
        player.draw(spriteBatch);
        spriteBatch.end();
    }

    private void updateCamera(){
        camera.position.x = player.getPositionX() + 5f;
        camera.position.y = player.getPositionY() + 5f;
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
    }

    @Override
    public void dispose(){
        spriteBatch.dispose();
        font.dispose();
        if (player != null) player.dispose();
        if (gameMap != null) gameMap.dispose();
        // System.exit(0);
    }
}
