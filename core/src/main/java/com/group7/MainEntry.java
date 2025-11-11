package com.group7;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.*;
import org.w3c.dom.Text;


/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class MainEntry extends Game {
    //camera and viewport
    private OrthographicCamera camera;
    private Viewport viewport;

    //view dimensions
    private static final float VIEW_WIDTH = 100f;
    private static final float VIEW_HEIGHT = 100f;

    //World Dimensions
    private float worldWidth = 500f;
    private float worldHeight = 500f;

    // Textures
    private Texture backgroundTexture;

    // Game Objects
    private Sprite background;
    private Player player;
    private Array<GameObject> staticObjects;


    // Rendering Objects
    private static SpriteBatch spriteBatch;
    private BitmapFont font;




    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, VIEW_WIDTH, VIEW_HEIGHT); //sets the camera (how we look at it) to the size of the view we want.
        // applies the viewport and sets it to the right dimensions. Also links the camera we made to that viewport (window)
        viewport = new ExtendViewport(VIEW_WIDTH, VIEW_HEIGHT, camera);

        spriteBatch = new SpriteBatch(); //creates the set of sprites to be added
        font = new BitmapFont(); //creates a font and sets color
        font.setColor(Color.WHITE);

        loadTextures(); // loads all the textures of the game and makes them sprites.

        createBackground(); // applies the background to the game world


        // create instance of new player at location 250,250
        player = new Player(worldWidth / 2, worldHeight / 2);

//        createStaticObjects();
    }


    @Override
    public void render(){ //this is what runs every frame
        float delta = Gdx.graphics.getDeltaTime(); // gets the base time regardless of FPS
        player.update(delta, worldWidth, worldHeight); // sets the players location
        ScreenUtils.clear(Color.BLACK); // clears the screen between each frame
        viewport.apply(); // sets the viewport
        updateCamera(); // makes sure the cam follows player
        spriteBatch.setProjectionMatrix(camera.combined); // not sure, but it works

        camera.update(); // makes sure the camera is still set
        spriteBatch.begin(); // need this to start drawing sprites

        background.draw(spriteBatch); // applies background to world
        player.draw(spriteBatch);
        spriteBatch.end(); // when you are done drawing sprites
    }

    //this loads in all the textures for the objects, player and background.
    private void loadTextures(){
        backgroundTexture = new Texture("Maps/background.png");
    }

    private void createBackground(){
        //make the sprite of the background
        background = new Sprite(backgroundTexture);

        //scale the background to cover the whole "World"
        background.setSize(worldWidth, worldHeight);
        background.setPosition(0,0);
    }

    private void updateCamera(){
         //make camera follow player
         camera.position.x = player.x + 5;
         camera.position.y = player.y + 5;

    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
    }

    @Override
    public void dispose(){ // gets rid of all the shit after we're done
        spriteBatch.dispose();
        font.dispose();
        backgroundTexture.dispose();
    }

//    private void createStaticObjects(){
        // this method will be used to add objects to the map as gameObjects
        // will need to set their rectangles in order to get collision with the player
        // ex:         staticObjects.add(new GameObject(treeTexture, 200, 300, 64, 96, Layer.FOREGROUND));
//    }

} // end MainEntry
