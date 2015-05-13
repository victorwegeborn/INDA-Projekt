package com.mygdx.screens;

import com.badlogic.gdx.Game;

public class TestGame extends Game {

	@Override
	public void create() {
		setScreen(new MainMenu());
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void resize(int width, int height){
		super.resize(width, height);
	}

	@Override
	public void dispose(){
		super.dispose();
	}
	
	@Override
	public void pause(){
		super.pause();
	}
	
	@Override
	public void resume(){
		super.resume();
	}
	
	
	
	
}
