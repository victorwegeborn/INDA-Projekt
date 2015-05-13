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

	public void beginContact(Contact c) {
		Fixture fa = c.getFixtureA(); 
		Fixture fb = c.getFixtureB();
		
		System.out.println(fa + " " + " " + fb);
		//Use f.getUserData() to identify the fixtures.
		//Note that order of fixtures vary, i.e. "player" is not always Fixture A
		if(fa.getUserData() != null && fa.getUserData().equals("player")){
			//DO STUFF
		}
		
		if(fb.getUserData() != null && fb.getUserData().equals("player")){
			//DO STUFF
		}
	}

 	public void endContact(Contact c) {	
	}

	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	public void postSolve(Contact contact, ContactImpulse impulse) {		
	}
	

}
