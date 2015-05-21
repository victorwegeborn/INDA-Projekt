package com.mygdx.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.mygdx.game.CoreGame;
import com.mygdx.game.ClientEngine;
import com.mygdx.game.GameClient;
import com.mygdx.game.GameServer;
import com.mygdx.game.SoundManager;

public class MainGame extends Game{

 public MainGame game;
 public GameClient client;
 public GameServer server;

@Override
public void create() {
	game = this;
	setScreen(new com.mygdx.gameMenu.MainMenu(game));
	}

public void PlayBGMusic(Sound sound){
	SoundManager.StopAllBG();
	sound.play();
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
