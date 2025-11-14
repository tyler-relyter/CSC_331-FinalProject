
package com.group7;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Map {
    private OrthogonalTiledMapRenderer mapRenderer;
    private TiledMap tiledMap;
    private TiledMapTileLayer groundLayer;
    private TiledMapTileLayer objectLayer;
    private AssetManager assetManager;

    // values read from the TMX
    private int tileWidth;
    private int tileHeight;
    private int mapWidthTiles;
    private int mapHeightTiles;

    private float unitScale;
    private float worldWidthTiles;
    private float worldHeightTiles;
    private float worldWidthPixels;
    private float worldHeightPixels;

    // visualScale multiplies the base unitScale (1 / tileWidth).
    private float visualScale = 1f;

    public Map(String mapPath) {
        this(mapPath, 1f);
    }

    // New ctor: pass visualScale > 1 to make tiles appear larger.
    public Map(String mapPath, float visualScale) {
        this.visualScale = visualScale <= 0f ? 1f : visualScale;

        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new com.badlogic.gdx.maps.tiled.TmxMapLoader());
        assetManager.load(mapPath, TiledMap.class);
        assetManager.finishLoading();

        this.tiledMap = assetManager.get(mapPath, TiledMap.class);
        if (this.tiledMap == null) throw new GdxRuntimeException("Failed to load tiled map: " + mapPath);

        // read properties from the TMX so scaling/size match the source map
        MapProperties props = tiledMap.getProperties();
        this.tileWidth = props.get("tilewidth", Integer.class);
        this.tileHeight = props.get("tileheight", Integer.class);
        this.mapWidthTiles = props.get("width", Integer.class);
        this.mapHeightTiles = props.get("height", Integer.class);

        // compute unitScale so 1 world unit == 1 tile times visualScale
        this.unitScale = (1f / (float) tileWidth) * this.visualScale;

        this.worldWidthTiles = mapWidthTiles;
        this.worldHeightTiles = mapHeightTiles;
        this.worldWidthPixels = mapWidthTiles * tileWidth;
        this.worldHeightPixels = mapHeightTiles * tileHeight;

        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        // optional: try to grab commonly named layers for later use (null-check safe)
        if (tiledMap.getLayers().getCount() > 0) {
            if (tiledMap.getLayers().get("Ground") instanceof TiledMapTileLayer) {
                this.groundLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Ground");
            }
            if (tiledMap.getLayers().get("Objects") instanceof TiledMapTileLayer) {
                this.objectLayer = (TiledMapTileLayer) tiledMap.getLayers().get("Objects");
            }
        }
    }

    // change visual scale at runtime (recreates renderer)
    public void setVisualScale(float visualScale) {
        if (visualScale <= 0f) return;
        this.visualScale = visualScale;
        float newUnitScale = (1f / (float) tileWidth) * this.visualScale;
        if (Math.abs(newUnitScale - this.unitScale) < 1e-6f) return;
        this.unitScale = newUnitScale;
        if (this.mapRenderer != null) {
            this.mapRenderer.dispose();
        }
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    // Render using an external camera (MainEntry's camera) so the map aligns with the rest of the world.
    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (assetManager != null) assetManager.dispose();
    }

    // Accessors for world size and tile info
    public float getWorldWidthTiles() { return worldWidthTiles; }
    public float getWorldHeightTiles() { return worldHeightTiles; }
    public float getWorldWidthPixels() { return worldWidthPixels; }
    public float getWorldHeightPixels() { return worldHeightPixels; }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }
    public int getMapWidthTiles() { return mapWidthTiles; }
    public int getMapHeightTiles() { return mapHeightTiles; }
    public float getUnitScale() { return unitScale; }
    public float getVisualScale() { return visualScale; }
}
