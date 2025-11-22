package com.group7;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color; //color constants
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont; //simple font for UI/debug text
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * A start menu screen that displays a title, start button, and an exit button.
 */
public class StartGUI extends ScreenAdapter {

    private final MainEntry game; //reference to the main game class to switch screens
    private Stage stage; //Stage that holds the buttons, labels, etc.

    public StartGUI(MainEntry game) {
        this.game = game;
    }

    @Override
    public void show() {
        //Creates a stage
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        //Creates a table to organize UI elements
        Table table = new Table();
        table.setFillParent(true); //table fills the stage
        stage.addActor(table); //add the table to the stage

        //Create a font that the labels and buttons use
        BitmapFont font = new BitmapFont(); // default font
        font.getData().setScale(2f); // make it bigger

        //Create the game title background
        Label.LabelStyle labelStyle = new Label.LabelStyle(); //button when not pressed
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        Label title = new Label("Idle Knight Escape", labelStyle);

        //Create textures for button backgrounds
        Pixmap pixmapUp = new Pixmap(200, 50, Pixmap.Format.RGBA8888); //button when not pressed
        pixmapUp.setColor(Color.DARK_GRAY);
        pixmapUp.fill();

        Pixmap pixmapDown = new Pixmap(200, 50, Pixmap.Format.RGBA8888); //button when pressed
        pixmapDown.setColor(Color.GRAY);
        pixmapDown.fill();

        //Defining button style
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new Texture(pixmapUp)); //not pressed
        buttonStyle.down = new TextureRegionDrawable(new Texture(pixmapDown)); //pressed
        buttonStyle.font = font; //button text font
        buttonStyle.fontColor = Color.WHITE;

        //Create start and exit buttons
        TextButton playButton = new TextButton("Start", buttonStyle);
        TextButton exitButton = new TextButton("Exit", buttonStyle);

        pixmapUp.dispose();
        pixmapDown.dispose();

        // Add the title, play, and exit elements to the table
        table.add(title).padBottom(50).row(); //title at the top
        table.add(playButton).padBottom(20).row(); //start button
        table.add(exitButton); //exit button

        // Button actions
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game)); // start game
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit(); //exit the application
            }
        });
    }

    @Override
    public void render(float delta) {
        //Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); //dark gray
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update the stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        //Update the stage when the window is resized
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
