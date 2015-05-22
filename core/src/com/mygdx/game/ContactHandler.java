package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.gameData.ItemData;
import com.mygdx.gameData.PlayerData;
import com.mygdx.NGame.NNetwork.PowerUpSound;


/**
 * This class handles the collisions that happen in 
 * the world. Each Contact holds information about
 * the fixtures that collide.
 * @author danhemgren
 *
 */
public class ContactHandler implements ContactListener {

	NetworkEngine server;
	
	public ContactHandler(NetworkEngine server){
		this.server = server;
	}
	
	public ContactHandler(){

	}
	
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
			PlayerData p = (PlayerData)other.getBody().getUserData();
			
			if(!p.Immortal())
				p.Kill();
			
			return;
		}
		
		// Fire hits other bomb -> bomb detonates
		
		System.out.println("other category: " + otherCategory + " BIT_ITEM: " + B2DVars.BIT_ITEM);
		// Fire hits item -> item is destroyed
		if(otherCategory == B2DVars.BIT_ITEM){
			System.out.println("Item destroyed");
			ItemData i = (ItemData)other.getBody().getUserData();
			i.FlagReset();
			return;
		}
			
		
	}
	
	private void HandleItem(Fixture a, Fixture b){
		PlayerData player;
		ItemData item;
		PowerUpSound p = new PowerUpSound();
		
		if(a.getFilterData().categoryBits == B2DVars.BIT_PLAYER){
			player = (PlayerData)a.getBody().getUserData();
			item = (ItemData)b.getBody().getUserData();
			
			if(item.ItemType() == B2DVars.FIRE_POWERUP){
				if(server != null){
				player.IncrementFirePower();
				item.FlagReset();
				p.bomb = false;
				server.server.sendToAllTCP(p);
				return;
				}
			}
				
			if(item.ItemType() == B2DVars.BOMB_POWERUP){
				if(server != null){
				player.IncrementBombCapacity();
				item.FlagReset();
				p.bomb = true;
				server.server.sendToAllTCP(p);
				return;
				}
			}
		}
		
		if(b.getFilterData().categoryBits == B2DVars.BIT_PLAYER){
			player = (PlayerData)b.getBody().getUserData();
			item = (ItemData)a.getBody().getUserData();
			
			if(item.ItemType() == B2DVars.FIRE_POWERUP){
				if(server != null){
				player.IncrementFirePower();
				item.FlagReset();
				p.bomb = false;
				server.server.sendToAllTCP(p);
				return;
				}
			}
				
			if(item.ItemType() ==  B2DVars.BOMB_POWERUP){
				if(server != null){
				player.IncrementBombCapacity();
				item.FlagReset();
				p.bomb = true;
				server.server.sendToAllTCP(p);
				return;
				}
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
				//DO STUFF
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
