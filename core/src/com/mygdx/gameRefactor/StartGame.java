package com.mygdx.gameRefactor;

import com.badlogic.gdx.Game;

public class StartGame extends Game {

	@Override
	public void create() {
		setScreen(new GameRefactor(this));
	}

	@Override
	public void render() {
		super.render();
	}
}
