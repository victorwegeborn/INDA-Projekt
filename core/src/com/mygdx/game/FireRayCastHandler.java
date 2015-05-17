package com.mygdx.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;

public class FireRayCastHandler implements RayCastCallback {
	
	public boolean hasCollided; 
	public Vector2 hitPoint;
	
	@Override
	public float reportRayFixture(Fixture fixture, Vector2 point,
			Vector2 normal, float fraction) {
		
		INDAGame.DrawSquare(point.x, point.y, Color.RED);
		
		short fixtureCategory = fixture.getFilterData().categoryBits;
		hitPoint = point;
		
		//System.out.println("Point: " + point + " Fixture: " + fixture.getUserData());
		//System.out.println(fixtureCategory);
		
		//Fire should _only_ raycast for walls (4) and boxes (8). (Items / players handled in ContactHandler)
		hasCollided = fixtureCategory == 4 || fixtureCategory == 8;
		
		//If fixture is box -> destroy box. 40% chance of random item spawn
		if(fixtureCategory == B2DVars.BIT_BOX){
			DestroyBox(fixture);
			
			//Spawn random item if i < 40
			Random r = new Random();
			int i = r.nextInt(100);
			if(i < B2DVars.DROP_RATE){
					if(i < B2DVars.DROP_RATE / 2){
					INDAGame.PlacePowerUp(B2DVars.BOMB_POWERUP, fixture.getBody().getPosition());
					}
					
					if(i >= B2DVars.DROP_RATE / 2){
					INDAGame.PlacePowerUp(B2DVars.FIRE_POWERUP, fixture.getBody().getPosition());
					}
				}
		}
		
		if(fixtureCategory == B2DVars.BIT_BOMB){
			Bomb b = (Bomb) fixture.getBody().getUserData();
			b.Detonate();
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
		SpawnRandomItem(f.getBody().getPosition());  //Spawn random item at box position
		
		//Deactivate body. All inactive boxes are removed from render queue by design.
		f.getBody().setActive(false); 
	}
	
	private void SpawnRandomItem(Vector2 position){
		//TODO: Implement random item spawn
	}
		

}
