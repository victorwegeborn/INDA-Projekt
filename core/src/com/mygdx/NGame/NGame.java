package com.mygdx.NGame;

import com.badlogic.gdx.Game;

public class NGame extends Game {

	@Override
	public void create() {
		setScreen(new NMainMenu(this));
	}
	
	@Override
	public void render() {
		super.render();
	}
}
