package com.mygdx.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.mygdx.game.CoreGame;
import com.mygdx.game.ClientEngine;

public class LocalGameWrapper extends Game{


@Override
public void create() {
	Screen s = new CoreGame();
	setScreen(s);
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
