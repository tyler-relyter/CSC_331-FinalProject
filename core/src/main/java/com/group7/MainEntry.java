package com.group7;

import com.badlogic.gdx.Game;

/**
 Main entry point for the LibGDX application.
 This class extends com.badlogic.gdx.Game which is a convenience
 application listener that manages a single Screen instance at a time.
*/
public class MainEntry extends Game {

    /**
     create()
     - Called once by LibGDX when the application is created.
     - Set the initial Screen here so the Start GUI is shown first.
     - Avoid putting heavy game initialization here if that work belongs
       inside a game screen (GameScreen) or a loading screen.
    */
    @Override
    public void create() {
        // Instantiate and show the start/menu screen as the first screen.
        // Pass this Game instance so the StartGUI can switch to other screens.
        setScreen(new StartGUI(this));
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
