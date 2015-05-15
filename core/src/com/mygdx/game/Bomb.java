package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bomb {
	private int firePower;
	private float timeToDetonate;
	private float timer;
	
	//Pool variables
	public boolean active;
	private Vector2 poolPosition;

	
	public Body body; //Body for easy positioning in world
	
	public State state;
	
	//***Animations***
	private Animation tickingAnim;
	private Animation blowUpAnim;
	private static float framerate = 1/24f; 
	//
	
	public enum State{
		Ticking, Exploding, Idle
	}
	
	
	public Bomb(int firePower, float timeToDetonate, World world, Vector2 poolPosition){
		
		this.poolPosition = poolPosition;
		active = false;
		
		this.timeToDetonate = timeToDetonate;
		timer = timeToDetonate;
		
		TextureAtlas spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/items/items.txt"));
		
		BodyDef bdef = new BodyDef();
		body = world.createBody(bdef);
		body.setType(BodyType.KinematicBody); // Set bomb bodies to ignore physics
		body.setTransform(poolPosition, 0); //Set body at pool position
		
		
		state = State.Idle;
		this.firePower = firePower;
		tickingAnim = new Animation(framerate, spriteSheet.findRegion("bomb"));
	
	}
	
	public int getFirePower(){
		return firePower;
	}
	
	public void setFirePower(int f){
		firePower = f;
	}
	
	public void update(float dt){
		timer -= dt;
		
		if(timer <= 0){
			Detonate();
			Reset();
		}
	}
	
	public void Detonate(){
		//TODO: Explosion mechanic
	}
	
	public void Reset(){
		//TODO: Reset to pool
		active = false;
		timer = timeToDetonate;
	}

	public Animation Animation(){		
		switch(state){
		case Idle: return tickingAnim;
		
		case Ticking: return tickingAnim;
		
		case Exploding: return blowUpAnim;
		
		default: return tickingAnim;
		}
	}

}
