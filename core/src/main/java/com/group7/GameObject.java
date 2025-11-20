package com.group7;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public class GameObject {
    private MapObject gameObject;
    private boolean blocked;
    private Rectangle bounds;
    private float unitScale;

    public GameObject(MapObject obj, float unitScale) {
        this.gameObject = gameObject;
        this.unitScale = unitScale;
        this.bounds = calculateBounds();
        this.blocked = getBlockedProperty();
    }

    private Rectangle calculateBounds() {
        MapProperties props = gameObject.getProperties();
        if (gameObject instanceof TextureMapObject) {
            TextureMapObject tmo = (TextureMapObject) gameObject;
            TextureRegion region = tmo.getTextureRegion();
            if (region != null) {
                float x = tmo.getX() * unitScale;
                float y = tmo.getY() * unitScale;
                float w = region.getRegionWidth() * unitScale * tmo.getScaleX();
                float h = region.getRegionHeight() * unitScale * tmo.getScaleY();
                return new Rectangle(x, y, w, h);
            }
        } else if (gameObject instanceof TiledMapTileMapObject) {
            TiledMapTileMapObject tto = (TiledMapTileMapObject) gameObject;
            TiledMapTile tile = tto.getTile();
            if (tile != null) {
                TextureRegion region = tile.getTextureRegion();
                float x = tto.getX() * unitScale;
                float y = tto.getY() * unitScale;
                float w = (region != null ? region.getRegionWidth() : 16) * unitScale * tto.getScaleX(); // Assuming default tileWidth if null
                float h = (region != null ? region.getRegionHeight() : 16) * unitScale * tto.getScaleY();
                return new Rectangle(x, y, w, h);
            }
        } else if (gameObject instanceof RectangleMapObject) {
            RectangleMapObject rmo = (RectangleMapObject) gameObject;
            Rectangle r = rmo.getRectangle();
            return new Rectangle(r.x * unitScale, r.y * unitScale, r.width * unitScale, r.height * unitScale);
        } else {
            // Fallback for other object types
            float x = props.containsKey("x") ? Float.parseFloat(props.get("x").toString()) * unitScale : 0;
            float y = props.containsKey("y") ? Float.parseFloat(props.get("y").toString()) * unitScale : 0;
            float w = props.containsKey("width") ? Float.parseFloat(props.get("width").toString()) * unitScale : 16 * unitScale;
            float h = props.containsKey("height") ? Float.parseFloat(props.get("height").toString()) * unitScale : 16 * unitScale;
            return new Rectangle(x, y, w, h);
        }
        return null;
    }

    private boolean getBlockedProperty() {
        MapProperties props = gameObject.getProperties();
        if (props.containsKey("blocked")) {
            Object val = props.get("blocked");
            return (val instanceof Boolean) ? (Boolean) val : Boolean.parseBoolean(val.toString());
        }
        return false;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
        gameObject.getProperties().put("blocked", blocked);
    }

    public boolean overlaps(Rectangle rect) {
        return bounds != null && bounds.overlaps(rect);
    }

    public boolean contains(float x, float y) {
        return bounds != null && bounds.contains(x, y);
    }

    public void render(Batch batch) {
        if (gameObject instanceof TextureMapObject) {
            TextureMapObject tmo = (TextureMapObject) gameObject;
            TextureRegion region = tmo.getTextureRegion();
            if (region != null) {
                float x = tmo.getX() * unitScale;
                float y = tmo.getY() * unitScale;
                float originX = tmo.getOriginX() * unitScale;
                float originY = tmo.getOriginY() * unitScale;
                float width = region.getRegionWidth() * unitScale;
                float height = region.getRegionHeight() * unitScale;
                float scaleX = tmo.getScaleX();
                float scaleY = tmo.getScaleY();
                float rotation = tmo.getRotation();
                batch.draw(region, x, y, originX, originY, width, height, scaleX, scaleY, rotation);
            }
        }

    }
}
