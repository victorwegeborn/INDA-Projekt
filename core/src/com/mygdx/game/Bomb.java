package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Bomb {
	private int firePower;
	private float timeToDetonate;
	private float timer;
	
	//Pool variables
	public boolean active;
	public boolean detonate;
	private Vector2 poolPosition;
	public Vector2 detonatePosition;

	
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
		FixtureDef fdef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 0.2f, new Vector2(0.5f, 0.5f), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_BOMB;
		fdef.filter.maskBits = B2DVars.BIT_FIRE;
		body.createFixture(fdef);
		body.setUserData(this); // Store reference to this bomb in world body
		shape.dispose();
		
		
		
		state = State.Idle;
		this.firePower = firePower;
		tickingAnim = new Animation(framerate, spriteSheet.findRegion("bomb"));
	
	}
	
	public int GetFirePower(){
		return firePower;
	}
	
	public void SetFirePower(int f){
		firePower = f;
	}
	
	public void Update(float dt){
		timer -= dt;
		
		if(timer <= 0){
			detonate = true;
			detonatePosition = body.getPosition();
			Reset();
		}
	}
	
	/**
	 * Flag this bomb for detonation at its position
	 * and then return it to the pool.
	 */
	public void Detonate(){
		detonatePosition = body.getPosition();
		detonate = true;
		Reset();
	}
	
	public void Reset(){
		//TODO: Reset to pool
		active = false;
		timer = timeToDetonate;
		
		//Return to pool position in world space
		body.setTransform(poolPosition, 0);
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
