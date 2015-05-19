package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gameData.ItemData;

public abstract class Item {

	public boolean active;
	public boolean pickedUp;
	public Body body;
	protected Vector2 poolPosition;
	protected Animation animation;
	protected int type;
	protected float animTimer;
	protected ItemData data;

	
	public Item(World world, Vector2 poolPosition, int type){
		this.type = type;
		this.poolPosition = poolPosition;
		BodyDef bdef = new BodyDef();
		body = world.createBody(bdef);
		body.setType(BodyType.KinematicBody); 
		body.setTransform(poolPosition, 0); //Set body at pool position
		FixtureDef fdef = new FixtureDef();
		fdef.isSensor = true; // Set items to be sensors
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 0.2f, new Vector2(0.5f, 0.5f), 0);
		fdef.shape = shape;
		fdef.filter.categoryBits = B2DVars.BIT_ITEM;
		fdef.filter.maskBits = B2DVars.BIT_FIRE | B2DVars.BIT_PLAYER;
		body.createFixture(fdef);
		shape.dispose();	
	}
	
	public TextureRegion Animation(){
		return animation.getKeyFrame(animTimer);
	}
	
	public void Update(float dt){
		//If item has been flagged for reset in world -> reset item
		if(data.ResetFlagged())
			Reset();
		
		animTimer += dt;
		UpdateItemData();
	}
	
	private void UpdateItemData(){
		data.SetPosition(body.getPosition().x, body.getPosition().y);
		data.SetActive(active);
		data.SetAnimTimer(animTimer);
	}
	
	public void Reset(){
		animTimer = 0f;
		active = false;
		pickedUp = false;
		body.setTransform(poolPosition, 0);
		data.UnflagReset();
		UpdateItemData();
	}
	
	public void PickUp(){
		pickedUp = true;
	}
	
	public int GetType(){
		return type;
	}
	
	public ItemData GetData(){
		return data;
	}
	
	public float AnimTimer(){
		return animTimer;
	}
	
}
