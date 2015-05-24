package com.mygdx.gameData;

import com.mygdx.game.B2DVars;

public class BombData extends ObjectData{
	
	private boolean detonate;
	private float detonateX; //position to detonate
	private float detonateY;
	private byte firePower;
	
	public BombData(){
		type = B2DVars.BIT_BOMB;
		detonate = false;
		detonateX = 0f;
		detonateY = 0f;
		x = 0f;
		y = 0f;
		active = false;
		
	}
	
	public BombData(byte type, float x, float y, boolean active){
		this.type = type;
		this.x = x;
		this.y = y;
		this.active = active;
		
		detonateX = 0f;
		detonateY = 0f;
		detonate = false;
	}
	
	public void SetDetonatePosition(float x, float y){
		detonateX = x;
		detonateY = y;
	}
	
	public float DetonatePositionX(){
		return detonateX;
	}
	
	public float DetonatePositionY(){
		return detonateY;
	}
	
	public void SetDetonate(boolean detonate){
		this.detonate = detonate;
	}
	
	public boolean Detonate(){
		return detonate;
	}
	
	public void FlagDetonation(){
		detonate = true;
	}
	
	public void UnflagDetonation(){
		detonate = false;
	}
	
	public int FirePower(){
		return firePower;
	}
	
}
