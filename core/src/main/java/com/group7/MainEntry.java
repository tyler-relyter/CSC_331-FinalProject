package com.group7;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;

/**
 Main entry point for the LibGDX application.
 This class extends com.badlogic.gdx.Game which is a convenience
 application listener that manages a single Screen instance at a time.
*/
public class MainEntry extends Game {
    private Music bgMusic;

    /**
     create()
     - Called once by LibGDX when the application is created.
     - Set the initial Screen here so the Start GUI is shown first.
     - Avoid putting heavy game initialization here if that work belongs
       inside a game screen (GameScreen) or a loading screen.
    */
    @Override
    public void create() {
        // Load music from assets
        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("Audio/menu_theme.mp3"));
        bgMusic.setLooping(true); // loop background music

        // Instantiate and show the start/menu screen as the first screen.
        // Pass this Game instance so the StartGUI can switch to other screens.
        setScreen(new StartGUI(this));
    }

    // Called by GameScreen to start/resume music
    public void playMusic() {
        if (bgMusic != null && !bgMusic.isPlaying()) {
            bgMusic.play();
        }
    }

    // Called by EndGUI to stop music
    public void stopMusic() {
        if (bgMusic != null && bgMusic.isPlaying()) {
            bgMusic.stop();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (bgMusic != null) {
            bgMusic.dispose();
        }
    }
    /**
     createGameScreen()
     - Optional factory method that StartGUI (or any other UI) can call
       to create the main gameplay screen.
     - Keeping a factory here centralizes construction logic and makes
       testing / swapping implementations easier.
     - This does not set the screen by itself; caller should call setScreen(...)
       if immediate switch is desired.
    */
    public GameScreen createGameScreen() {
        // Create and return a new GameScreen using this Game for context.
        return new GameScreen(this);
    }
}
