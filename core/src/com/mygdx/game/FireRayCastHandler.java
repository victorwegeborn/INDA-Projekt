package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.gameData.BombData;
import com.mygdx.gameData.ItemData;

public class FireRayCastHandler implements RayCastCallback {
	
	public boolean hasCollided; 
	public Vector2 hitPoint;
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
				
		short fixtureCategory = fixture.getFilterData().categoryBits;
		hitPoint = point;
		
		//System.out.println("Point: " + point + " Fixture: " + fixture.getUserData());
		//System.out.println(fixtureCategory);
		
		//Fire should _only_ raycast for walls (4) and boxes (8). (Items / players handled in ContactHandler)
		hasCollided = fixtureCategory == 4 || fixtureCategory == 8;
		
		//If fixture is box -> destroy box. 40% chance of random item spawn
		if(fixtureCategory == B2DVars.BIT_BOX){
			DestroyBox(fixture);
		}
					
		if(fixtureCategory == B2DVars.BIT_BOMB){
			BombData b = (BombData)fixture.getBody().getUserData();
			b.FlagDetonation();
		}
		
		if(fixtureCategory == B2DVars.BIT_ITEM){
			ItemData i = (ItemData)fixture.getBody().getUserData();
			i.FlagReset();
		}
		
		return hasCollided ? 0 : -1;
	}
	
	/**
	 * This is necessary in order to use the same 
	 * FireRayCastHandler for several checks at runtime.
	 */
	public void ResetCollisionCheck(){
		hasCollided = false;
		hitPoint = null;
	}
	
	private void DestroyBox(Fixture f){
		ItemPlacer.SpawnRandomPowerUp(f.getBody().getPosition());  //Spawn random item at box position
		
		//Deactivate body. All inactive boxes are removed from render queue by design.
		f.getBody().setActive(false); 
	}
	

		

}
