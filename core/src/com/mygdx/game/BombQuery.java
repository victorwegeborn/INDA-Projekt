package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

public class BombQuery implements QueryCallback{
	
	public boolean tileHasBomb;
	
	public BombQuery(){
		tileHasBomb = false;
	}

	@Override
	public boolean reportFixture(Fixture fixture) {
		if(fixture.getFilterData().categoryBits == B2DVars.BIT_BOMB){
			tileHasBomb = true;
		}
	
		
		return false;
	}
	
	public void Reset(){
		tileHasBomb = false;
	}

}
