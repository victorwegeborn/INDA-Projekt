package com.mygdx.gameRefactor;

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
		//System.out.println("Cat a: " + aCategory + " Cat b: " + bCategory);
		
		if(aCategory == B2DVars.BIT_FIRE || bCategory == B2DVars.BIT_FIRE){
			if(aCategory == B2DVars.BIT_FIRE)
				HandleFire(a, b);		
			else
				HandleFire(b, a);
		}
		
		// If one category is player and the other is item -> handle item pickup
		if(aCategory == B2DVars.BIT_ITEM || bCategory == B2DVars.BIT_ITEM){
			if(aCategory == B2DVars.BIT_PLAYER || bCategory == B2DVars.BIT_PLAYER)
				HandleItem(c.getFixtureA(), c.getFixtureB());
		}
		
	}

	
	private void HandleFire(Fixture fire, Fixture other){
		//System.out.println(other.getFilterData().categoryBits);
		short otherCategory = other.getFilterData().categoryBits;
	
		
		// Box collision handled via raycasting. Walls not affected by fire.
		if(otherCategory == B2DVars.BIT_BOX || otherCategory == B2DVars.BIT_WALL)
			return;
		
		// Fire hits player -> player dies
		if(otherCategory == B2DVars.BIT_PLAYER){
			Player p = (Player)other.getBody().getUserData();
			
			if(!p.immortal)
				p.Kill();
			
			return;
		}
		
		// Fire hits other bomb -> bomb detonates
		if(otherCategory == B2DVars.BIT_BOMB){
			Bomb b = (Bomb)other.getBody().getUserData();
			b.Detonate();
			return;
		}
		
		// Fire hits item -> item is destroyed
		if(otherCategory == B2DVars.BIT_ITEM){
			Item i = (Item)other.getBody().getUserData();
			i.Reset();
			return;
		}
			
		
	}
	
	private void HandleItem(Fixture a, Fixture b){
		Player player;
		Item item;
		
		if(a.getFilterData().categoryBits == B2DVars.BIT_PLAYER){
			player = (Player)a.getBody().getUserData();
			item = (Item)b.getBody().getUserData();
			
			if(item.GetType() == B2DVars.FIRE_POWERUP){
				player.IncrementFirePower();
				item.PickUp();
				return;
			}
				
			if(item.GetType() == B2DVars.BOMB_POWERUP){
				player.IncrementBombCapacity();
				item.PickUp();
				return;
			}
		}
		
		if(b.getFilterData().categoryBits == B2DVars.BIT_PLAYER){
			player = (Player)b.getBody().getUserData();
			item = (Item)a.getBody().getUserData();
			
			if(item.GetType() == B2DVars.FIRE_POWERUP){
				player.IncrementFirePower();
				item.PickUp();
				return;
			}
				
			if(item.GetType() ==  B2DVars.BOMB_POWERUP){
				player.IncrementBombCapacity();
				item.PickUp();
				return;
			}
		}
	}
	
	@Override
 	public void endContact(Contact c) {	
 		Fixture fa = c.getFixtureA(); 
		Fixture fb = c.getFixtureB();
		short aCategory = fa.getFilterData().categoryBits;
		short bCategory = fb.getFilterData().categoryBits;
		
		if(aCategory == B2DVars.BIT_BOMB || bCategory == B2DVars.BIT_BOMB){
			if(aCategory == B2DVars.BIT_PLAYER || bCategory == B2DVars.BIT_PLAYER){
				System.out.println("Make solid");
			}
		}
		
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
