package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

    private final float width;
    private final float height;

    private final Vector2 position;
    private final Vector2 velocity;
    private final float speed;

    private Map map;
    private Player target;   // player to chase

    private static final float COLLISION_PADDING = 0.05f;

    private float stateTime;
    private Texture texture;

    public Enemy(float x, float y, Player target) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2(0, 0);
        this.speed = 60f;      // a bit slower than the player so it feels fair
        this.width = 15f;
        this.height = 15f;

        this.target = target;
        this.stateTime = 0f;

        // super simple visual for now – replace with your own sprite/animation later
        this.texture = new Texture(Gdx.files.internal("Characters/bluefire.png"));
    }

    public void setMap(Map map) {
        this.map = map;
    }

    /** Basic AI: set velocity toward the player. */
    private void updateAI() {
        velocity.set(0, 0);

        if (target == null) {
            return;
        }

        float dx = target.getPositionX() - position.x;
        float dy = target.getPositionY() - position.y;

        // Don't jitter if we're basically on top of the player
        if (dx * dx + dy * dy < 1f) {
            return;
        }

        Vector2 toPlayer = new Vector2(dx, dy);
        toPlayer.nor();                // direction only
        toPlayer.scl(speed);           // scale by enemy speed
        velocity.set(toPlayer);
    }

    /**
     * Update movement + collision.
     * This is basically your Player.update() movement code,
     * but driven by updateAI() instead of keyboard.
     */
    public void update(float delta, float worldWidth, float worldHeight) {
        // AI decides velocity
        updateAI();

        Vector2 previous = new Vector2(this.position);

        float dx = this.velocity.x * delta;
        float dy = this.velocity.y * delta;

        // ---- X-axis move + collision (same style as Player) ----
        this.position.x += dx;
        if (map != null) {
            int[] blocked = findFirstBlockedTileForBounds();
            if (blocked != null) {
                float visualScale = map.getVisualScale();
                int tx = blocked[0];
                float tileStartX = tx * visualScale;
                float tileEndX = tileStartX + visualScale;
                if (dx > 0) {
                    this.position.x = tileStartX - this.width - COLLISION_PADDING;
                } else if (dx < 0) {
                    this.position.x = tileEndX + COLLISION_PADDING;
                } else {
                    this.position.x = previous.x;
                }
            }
        }

        // ---- Y-axis move + collision ----
        this.position.y += dy;
        if (map != null) {
            int[] blocked = findFirstBlockedTileForBounds();
            if (blocked != null) {
                float visualScale = map.getVisualScale();
                int ty = blocked[1];
                float tileStartY = ty * visualScale;
                float tileEndY = tileStartY + visualScale;
                if (dy > 0) {
                    this.position.y = tileStartY - this.height - COLLISION_PADDING;
                } else if (dy < 0) {
                    this.position.y = tileEndY + COLLISION_PADDING;
                } else {
                    this.position.y = previous.y;
                }
            }
        }

        // clamp to world bounds like Player
        this.position.x = MathUtils.clamp(this.position.x, 0, worldWidth - width);
        this.position.y = MathUtils.clamp(this.position.y, 0, worldHeight - height);

        stateTime += delta;
    }

    /** Same idea as Player.findFirstBlockedTileForBounds(), reused. */
    private int[] findFirstBlockedTileForBounds() {
        if (map == null) return null;

        float left = this.position.x;
        float right = this.position.x + this.width;
        float bottom = this.position.y;
        float top = this.position.y + this.height;
        float centerX = this.position.x + this.width * 0.5f;
        float centerY = this.position.y + this.height * 0.5f;

        float[] samples = new float[] {
            left, bottom,
            right, bottom,
            left, top,
            right, top,
            centerX, centerY
        };

        for (int i = 0; i < samples.length; i += 2) {
            float sx = samples[i];
            float sy = samples[i + 1];
            try {
                if (map.isBlocked(sx, sy)) {
                    int tileX = (int) Math.floor(sx / map.getVisualScale());
                    int tileY = (int) Math.floor(sy / map.getVisualScale());
                    return new int[]{tileX, tileY};
                }
            } catch (Exception e) {
                int tileX = (int) Math.floor(sx / map.getVisualScale());
                int tileY = (int) Math.floor(sy / map.getVisualScale());
                return new int[]{tileX, tileY};
            }
        }

        return null;
    }

    /** Draw enemy sprite. */
    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

    public float getX() { return position.x; }
    public float getY() { return position.y; }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }
}
