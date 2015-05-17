package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;

public class WorldQuery implements QueryCallback{
	
	public boolean occupied;
	
	@Override
	public boolean reportFixture(Fixture fixture) {
		String s = (String)fixture.getUserData();
		System.out.println(s);
		if(s == "box" || s == "wall")
				occupied = true;
		
		return true;
	}

}
