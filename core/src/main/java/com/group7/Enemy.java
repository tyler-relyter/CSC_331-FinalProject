package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {
    private final float width;
    private final float height;

    private Vector2 position; //sets enemy position
    private Rectangle damageArea; //rectangle for the enemy "hitbox"

    private float health;
    private Texture enemyTexture;
    private Sprite enemySprite;


    private Map map;

    public Enemy(float x, float y){
       this.width = 15f;
       this.height = 15f;
       this.position  = new Vector2(x, y); //sets position of the enemy on the map
       this.damageArea = new Rectangle();
       this.enemyTexture = new Texture(Gdx.files.internal("Enemy/test_enemy.png"));
       this.enemySprite = new Sprite(enemyTexture);
    }
    public void setMap(Map map){
        this.map = map;
    }

    public void update(){

    }

    public void draw(SpriteBatch spriteBatch){
        spriteBatch.draw(enemyTexture, this.position.x,this.position.y);
    }

    public float getPositionX(){
        return this.position.x;
    }
    public float getPositionY(){
        return this.position.y;
    }

}
