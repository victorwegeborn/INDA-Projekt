package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;


/**
 * This class handles the collisions that happen in 
 * the world. Each Contact holds information about
 * the fixtures that collide.
 * @author danhemgren
 *
 */
public class ContactHandler implements ContactListener {

	@Override
	public void beginContact(Contact c) {
		//System.out.println("COLLISION DETECTED");
		Fixture a = c.getFixtureA();
		Fixture b = c.getFixtureB();
		short aCategory = c.getFixtureA().getFilterData().categoryBits; 
		short bCategory = c.getFixtureB().getFilterData().categoryBits;
		
		if(aCategory == B2DVars.BIT_FIRE || bCategory == B2DVars.BIT_FIRE){
			if(aCategory == B2DVars.BIT_FIRE)
				HandleFire(a, b);		
			else
				HandleFire(b, a);
		}
		
	}

	
	private void HandleFire(Fixture fire, Fixture other){
		//System.out.println(other.getFilterData().categoryBits);
		short otherCategory = other.getFilterData().categoryBits;
	
		
		// Box collision handled via raycasting. Walls not affected by fire.
		if(otherCategory == B2DVars.BIT_BOX || otherCategory == B2DVars.BIT_WALL)
			return;
		
	
		if(otherCategory == B2DVars.BIT_PLAYER){
			Player p = (Player)other.getBody().getUserData();
			p.Kill();
		}
		
	}
	
	@Override
 	public void endContact(Contact c) {	
 		Fixture fa = c.getFixtureA(); 
		Fixture fb = c.getFixtureB();
		
		if(fa.getUserData() != null && fa.getUserData().equals("player")){
			//DO STUFF
		}
		
		if(fb.getUserData() != null && fb.getUserData().equals("player")){
			//DO STUFF
		}
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {		
	}
	

}
