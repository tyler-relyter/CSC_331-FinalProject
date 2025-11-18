package com.group7;

import com.badlogic.gdx.Game;

public class MainEntry extends Game {

    @Override
    public void create() {
        // Start with the menu screen
        setScreen(new StartGUI(this));
    }
}
