package com.mygdx.game;

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
		
		//TODO: IF fixture is box -> destroy box. Set box user data to the cell of that box?
		if(fixtureCategory == B2DVars.BIT_BOX)
			DestroyBox(fixture);
		
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
