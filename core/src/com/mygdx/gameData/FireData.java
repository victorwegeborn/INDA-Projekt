package com.mygdx.gameData;

import com.mygdx.game.B2DVars;
import com.mygdx.gameData.PlayerData;

public class FireData extends ObjectData {
	
	private float animTimer;
	private float animDuration;
	
	private byte state;
	
	public FireData(){
		type = (byte)B2DVars.BIT_FIRE;
		x = 0f;
		y = 0f;
		animTimer = 0f;
		animDuration = 0f;
		state = B2DVars.FIRE_MID;
		
	}
	
	public FireData(byte type, float x, float y, float animTimer, float animDuration){
		this.type = type;
		this.x = x;
		this.y = y;
		this.animTimer = animTimer;
		this.animDuration = animDuration;
		state = (byte)B2DVars.FIRE_MID;
	}
	
	
	public void SetState(byte state){
		this.state = state;
	}
	
	public byte GetState(){
		return state;
	}
	
	public float GetAnimTimer(){
		return animTimer;
	}
	
	public void SetAnimTimer(float animTimer){
		this.animTimer = animTimer;
	}
	

}
