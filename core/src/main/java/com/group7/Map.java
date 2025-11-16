package com.group7;

// Import libGDX classes for asset loading, rendering, and tile map handling
import com.badlogic.gdx.assets.AssetManager; // manages loading and unloading of game assets
import com.badlogic.gdx.graphics.OrthographicCamera; // 2D camera for rendering the game world
import com.badlogic.gdx.graphics.g2d.Batch; // interface for drawing 2D sprites and textures
import com.badlogic.gdx.graphics.g2d.TextureRegion; // represents a rectangular region of a texture
import com.badlogic.gdx.maps.MapLayer; // base class for map layers (tile layers, object layers, etc.)
import com.badlogic.gdx.maps.MapObject; // represents an individual object placed on a map
import com.badlogic.gdx.maps.MapProperties; // key-value properties attached to map elements
import com.badlogic.gdx.maps.objects.TextureMapObject; // map object that contains a texture/sprite
import com.badlogic.gdx.maps.tiled.TiledMap; // represents a TMX tiled map file
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer; // represents a layer containing tiles in a grid
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer; // renders orthogonal (top-down) tile maps
import com.badlogic.gdx.math.Rectangle; // rectangle shape used for collision detection
import com.badlogic.gdx.utils.GdxRuntimeException; // exception thrown when libGDX encounters an error

import java.util.ArrayList; // dynamic array for storing layer indices

/**
 * Map class handles loading, rendering, and collision detection for a Tiled TMX map.
 * It supports both tile-based layers and object layers for flexible map design.
 */
public class Map {
    // Renderer that draws the tiled map to the screen
    private OrthogonalTiledMapRenderer mapRenderer;

    // The loaded TMX map data structure
    private TiledMap tiledMap;

    // Reference to the "Ground" tile layer (walkable/unwalkable terrain tiles)
    private TiledMapTileLayer groundLayer;

    // Reference to the "Object" layer if it's implemented as a tile layer
    private TiledMapTileLayer objectTileLayer;

    // Reference to the "Object" layer if it's implemented as an object group (sprites/textures)
    private MapLayer objectsLayer;

    // Asset manager handles loading and disposal of the TMX file
    private AssetManager assetManager;

    // Pixel dimensions of each individual tile in the map
    private int tileWidth;  // width of one tile in pixels
    private int tileHeight; // height of one tile in pixels

    // Number of tiles across the entire map
    private int mapWidthTiles;  // map width measured in tiles
    private int mapHeightTiles; // map height measured in tiles

    // Unit scale converts pixel coordinates to world coordinates
    // Formula: unitScale = (1 / tileWidth) * visualScale
    private float unitScale;

    // World dimensions in "world units" (tiles scaled by visualScale)
    private float worldWidthTiles;  // map width in world units
    private float worldHeightTiles; // map height in world units

    // World dimensions in raw pixels (before any scaling)
    private float worldWidthPixels;  // map width in pixels
    private float worldHeightPixels; // map height in pixels

    // Visual scale multiplier: makes tiles appear larger (>1) or smaller (<1) on screen
    private float visualScale = 1f;

    // Array of layer indices that should be rendered (only tile layers)
    private int[] renderLayerIndices;

    /**
     * Constructor with default visual scale of 1.0
     * @param mapPath path to the TMX map file in the assets folder
     */
    public Map(String mapPath) {
        this(mapPath, 1f); // delegate to full constructor with scale = 1
    }

    /**
     * Full constructor that loads and initializes the map
     * @param mapPath path to the TMX map file
     * @param visualScale multiplier for tile size (6f makes tiles 6x larger)
     */
    public Map(String mapPath, float visualScale) {
        // Ensure visual scale is positive; default to 1 if invalid
        this.visualScale = visualScale <= 0f ? 1f : visualScale;

        // Create asset manager and register the TMX loader
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new com.badlogic.gdx.maps.tiled.TmxMapLoader());

        // Queue the TMX map for loading
        assetManager.load(mapPath, TiledMap.class);

        // Block until the map finishes loading
        assetManager.finishLoading();

        // Retrieve the loaded map from the asset manager
        this.tiledMap = assetManager.get(mapPath, TiledMap.class);

        // Throw error if map failed to load
        if (this.tiledMap == null) throw new GdxRuntimeException("Failed to load tiled map: " + mapPath);

        // Read map properties from the TMX file
        MapProperties props = tiledMap.getProperties();
        this.tileWidth = props.get("tilewidth", Integer.class);     // pixels per tile (width)
        this.tileHeight = props.get("tileheight", Integer.class);   // pixels per tile (height)
        this.mapWidthTiles = props.get("width", Integer.class);     // number of tiles wide
        this.mapHeightTiles = props.get("height", Integer.class);   // number of tiles tall

        // Calculate unit scale: converts pixels to world units
        // Example: if tileWidth=16 and visualScale=6, then unitScale = (1/16)*6 = 0.375
        this.unitScale = (1f / (float) tileWidth) * this.visualScale;

        // World dimensions in "world units" (number of tiles, effectively)
        this.worldWidthTiles = mapWidthTiles;
        this.worldHeightTiles = mapHeightTiles;

        // World dimensions in raw pixels (tiles * pixels per tile)
        this.worldWidthPixels = mapWidthTiles * tileWidth;
        this.worldHeightPixels = mapHeightTiles * tileHeight;

        // Create the map renderer with the calculated unit scale
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);

        // Build list of tile layer indices to render, and find specific named layers
        ArrayList<Integer> tileLayerIndices = new ArrayList<>();
        for (int i = 0; i < tiledMap.getLayers().getCount(); i++) {
            MapLayer layer = tiledMap.getLayers().get(i);
            String name = layer.getName();

            // Check if this layer is a tile layer (grid of tiles)
            if (layer instanceof TiledMapTileLayer) {
                tileLayerIndices.add(i); // add to render list

                // Store reference to "Ground" layer for collision checks
                if ("Ground".equals(name)) {
                    this.groundLayer = (TiledMapTileLayer) layer;
                }

                // Store reference to "Object" tile layer (if objects are tiles)
                if ("Object".equals(name)) {
                    this.objectTileLayer = (TiledMapTileLayer) layer;
                }
            } else {
                // Layer is an object group (contains sprites/shapes, not tiles)
                if ("Object".equals(name)) {
                    this.objectsLayer = layer;
                }
            }
        }

        // Convert ArrayList to primitive int array for renderer
        this.renderLayerIndices = new int[tileLayerIndices.size()];
        for (int i = 0; i < tileLayerIndices.size(); i++) {
            this.renderLayerIndices[i] = tileLayerIndices.get(i);
        }
    }

    /**
     * Updates the visual scale and recreates the renderer if scale changed
     * @param visualScale new scale multiplier (must be > 0)
     */
    public void setVisualScale(float visualScale) {
        // Ignore invalid scales
        if (visualScale <= 0f) {
            return;
        }

        this.visualScale = visualScale;

        // Recalculate unit scale with new visual scale
        float newUnitScale = (1f / (float) tileWidth) * this.visualScale;

        // Only recreate renderer if scale actually changed (avoid floating point errors)
        if (Math.abs(newUnitScale - this.unitScale) < 1e-6f) {
            return;
        }

        this.unitScale = newUnitScale;

        // Dispose old renderer to free GPU resources
        if (this.mapRenderer != null) {
            this.mapRenderer.dispose();
        }

        // Create new renderer with updated scale
        this.mapRenderer = new OrthogonalTiledMapRenderer(tiledMap, unitScale);
    }

    /**
     * Renders the map and any texture objects to the screen
     * @param camera the camera defining what part of the world is visible
     */
    public void render(OrthographicCamera camera) {
        // Configure renderer to use the camera's view
        mapRenderer.setView(camera);

        // Render only the tile layers (not object groups)
        if (renderLayerIndices != null && renderLayerIndices.length > 0) {
            mapRenderer.render(renderLayerIndices); // render specific layers
        } else {
            mapRenderer.render(); // render all layers
        }

        // Manually render TextureMapObjects from the object layer (if present)
        if (objectsLayer != null && objectsLayer.getObjects() != null && objectsLayer.getObjects().getCount() > 0) {
            // Get the renderer's sprite batch for drawing
            Batch batch = mapRenderer.getBatch();

            // Align batch's coordinate system with camera
            batch.setProjectionMatrix(camera.combined);

            batch.begin(); // start drawing sprites

            // Loop through all objects in the object layer
            for (MapObject obj : objectsLayer.getObjects()) {
                // Only draw objects that contain textures
                if (obj instanceof TextureMapObject) {
                    TextureMapObject textureMapObj = (TextureMapObject) obj;
                    TextureRegion region = textureMapObj.getTextureRegion();

                    if (region == null) continue; // skip if no texture

                    // Read object's position and dimensions (in pixels)
                    float x = textureMapObj.getX() * unitScale;           // convert pixels to world units
                    float y = textureMapObj.getY() * unitScale;
                    float originX = textureMapObj.getOriginX() * unitScale; // rotation origin
                    float originY = textureMapObj.getOriginY() * unitScale;
                    float width = region.getRegionWidth() * unitScale;     // texture width
                    float height = region.getRegionHeight() * unitScale;   // texture height
                    float scaleX = textureMapObj.getScaleX();              // horizontal scale
                    float scaleY = textureMapObj.getScaleY();              // vertical scale
                    float rotation = textureMapObj.getRotation();          // rotation in degrees

                    // Draw the texture with all transformations applied
                    batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
                }
            }

            batch.end(); // finish sprite drawing
        }
    }

    /**
     * Checks if a world position is blocked by any tile or object
     * @param worldX x coordinate in world units
     * @param worldY y coordinate in world units
     * @return true if blocked, false if passable
     */
    public boolean isBlocked(float worldX, float worldY) {
        // First check collision with objects in the object layer (sprites/textures)
        if (objectsLayer != null && objectsLayer.getObjects() != null) {
            // Loop through every object in the layer
            for (MapObject obj : objectsLayer.getObjects()) {
                if (obj instanceof TextureMapObject) {
                    TextureMapObject tmo = (TextureMapObject) obj;
                    MapProperties props = obj.getProperties();

                    // Check if object has the "blocked" property
                    if (props.containsKey("blocked")) {
                        Object val = props.get("blocked");

                        // Parse blocked value (could be Boolean or String)
                        boolean blocked = (val instanceof Boolean) ? (Boolean) val : Boolean.parseBoolean(val.toString());

                        // Only test collision if object is marked as blocked
                        if (blocked) {
                            TextureRegion region = tmo.getTextureRegion();
                            if (region != null) {
                                // Calculate object's bounding box in world units
                                float objX = tmo.getX() * unitScale;
                                float objY = tmo.getY() * unitScale;
                                float objWidth = region.getRegionWidth() * unitScale * tmo.getScaleX();
                                float objHeight = region.getRegionHeight() * unitScale * tmo.getScaleY();

                                // Create rectangle for collision test
                                Rectangle objBounds = new Rectangle(objX, objY, objWidth, objHeight);

                                // Check if query point is inside object's bounds
                                if (objBounds.contains(worldX, worldY)) {
                                    return true; // collision detected
                                }
                            }
                        }
                    }
                }
            }
        }

        // If no tile layers exist, position is not blocked
        if (groundLayer == null && objectTileLayer == null) {
            return false;
        }

        // Convert world coordinates to tile coordinates
        // Example: worldX=35, visualScale=6 → tileX = floor(35/6) = 5
        int tileX = (int) Math.floor(worldX / visualScale);
        int tileY = (int) Math.floor(worldY / visualScale);

        // Delegate to tile-based collision check
        return isBlocked(tileX, tileY);
    }

    /**
     * Checks if a tile position is blocked
     * @param tileX x coordinate in tile units
     * @param tileY y coordinate in tile units
     * @return true if blocked, false if passable
     */
    public boolean isBlocked(int tileX, int tileY) {
        // No layers means nothing to collide with
        if (groundLayer == null && objectTileLayer == null) {
            return false;
        }

        // Positions outside map bounds are always blocked
        if (tileX < 0 || tileY < 0 || tileX >= mapWidthTiles || tileY >= mapHeightTiles) {
            return true;
        }

        // Check the ground layer for blocked tiles
        if (groundLayer != null) {
            // Get the cell (tile) at this position
            TiledMapTileLayer.Cell cell = groundLayer.getCell(tileX, tileY);

            // Check if cell exists and contains a tile
            if (cell != null && cell.getTile() != null) {
                // Read tile properties
                MapProperties props = cell.getTile().getProperties();

                // Check for "blocked" property
                if (props.containsKey("blocked")) {
                    Object val = props.get("blocked");

                    // Handle Boolean type
                    if (val instanceof Boolean) {
                        if ((Boolean) val) return true;
                    }
                    // Handle String type (parse to boolean)
                    else if (Boolean.parseBoolean(val.toString())) {
                        return true;
                    }
                }
            }
        }

        // Check the object tile layer for blocked tiles
        if (objectTileLayer != null) {
            // Get the cell at this position
            TiledMapTileLayer.Cell cell = objectTileLayer.getCell(tileX, tileY);

            if (cell != null && cell.getTile() != null) {
                MapProperties props = cell.getTile().getProperties();

                // Check for "blocked" property
                if (props.containsKey("blocked")) {
                    Object val = props.get("blocked");

                    if (val instanceof Boolean) {
                        if ((Boolean) val) return true;
                    } else if (Boolean.parseBoolean(val.toString())) {
                        return true;
                    }
                }
            }
        }

        return false; // no blocking tiles found
    }

    /**
     * Sets the blocked state of a tile or object at a world position
     * This allows dynamic modification of collision (e.g., destroying obstacles)
     * @param worldX x coordinate in world units
     * @param worldY y coordinate in world units
     * @param blocked new blocked state (true = blocked, false = passable)
     */
    public void setBlocked(float worldX, float worldY, boolean blocked) {
        // First check if position matches any object in the object layer
        if (objectsLayer != null && objectsLayer.getObjects() != null) {
            for (MapObject obj : objectsLayer.getObjects()) {
                if (obj instanceof TextureMapObject) {
                    TextureMapObject tmo = (TextureMapObject) obj;
                    TextureRegion region = tmo.getTextureRegion();

                    if (region != null) {
                        // Calculate object's bounding box in world units
                        float objX = tmo.getX() * unitScale;
                        float objY = tmo.getY() * unitScale;
                        float objWidth = region.getRegionWidth() * unitScale * tmo.getScaleX();
                        float objHeight = region.getRegionHeight() * unitScale * tmo.getScaleY();

                        Rectangle objBounds = new Rectangle(objX, objY, objWidth, objHeight);

                        // If position is inside this object, update its blocked property
                        if (objBounds.contains(worldX, worldY)) {
                            obj.getProperties().put("blocked", blocked);
                            return; // done - found matching object
                        }
                    }
                }
            }
        }

        // No matching object found, check tile layers instead
        // Convert world coordinates to tile coordinates
        int tileX = (int) Math.floor(worldX / visualScale);
        int tileY = (int) Math.floor(worldY / visualScale);

        // Ignore positions outside map bounds
        if (tileX < 0 || tileY < 0 || tileX >= mapWidthTiles || tileY >= mapHeightTiles) {
            return;
        }

        // Try to update blocked property on object tile layer first
        if (objectTileLayer != null) {
            TiledMapTileLayer.Cell cell = objectTileLayer.getCell(tileX, tileY);

            if (cell != null && cell.getTile() != null) {
                // Update the tile's blocked property
                cell.getTile().getProperties().put("blocked", blocked);
                return; // done
            }
        }

        // Fall back to ground layer if object tile layer didn't have a tile there
        if (groundLayer != null) {
            TiledMapTileLayer.Cell cell = groundLayer.getCell(tileX, tileY);

            if (cell != null && cell.getTile() != null) {
                // Update the tile's blocked property
                cell.getTile().getProperties().put("blocked", blocked);
            }
        }
    }

    /**
     * Disposes all resources (map, renderer, assets) to prevent memory leaks
     * Always call this when the map is no longer needed
     */
    public void dispose() {
        if (tiledMap != null) tiledMap.dispose();       // free map data
        if (mapRenderer != null) mapRenderer.dispose(); // free renderer GPU resources
        if (assetManager != null) assetManager.dispose(); // free all loaded assets
    }

    // Getter methods for accessing map dimensions and properties

    /** @return map width in world units (tiles) */
    public float getWorldWidthTiles() { return worldWidthTiles; }

    /** @return map height in world units (tiles) */
    public float getWorldHeightTiles() { return worldHeightTiles; }

    /** @return map width in pixels */
    public float getWorldWidthPixels() { return worldWidthPixels; }

    /** @return map height in pixels */
    public float getWorldHeightPixels() { return worldHeightPixels; }

    /** @return width of one tile in pixels */
    public int getTileWidth() { return tileWidth; }

    /** @return height of one tile in pixels */
    public int getTileHeight() { return tileHeight; }

    /** @return map width measured in tiles */
    public int getMapWidthTiles() { return mapWidthTiles; }

    /** @return map height measured in tiles */
    public int getMapHeightTiles() { return mapHeightTiles; }

    /** @return unit scale used to convert pixels to world units */
    public float getUnitScale() { return unitScale; }

    /** @return visual scale multiplier applied to tile size */
    public float getVisualScale() { return visualScale; }
}
