package com.mygdx.screen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.mygdx.game.CoreGame;
import com.mygdx.game.GameClient;
import com.mygdx.game.GameServer;

public class ServerWrapper extends Game{


@Override
public void create() {
	setScreen(new GameServer());
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
