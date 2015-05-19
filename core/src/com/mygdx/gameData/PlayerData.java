package com.mygdx.gameData;

import com.mygdx.game.B2DVars;

public class PlayerData extends ObjectData{

	private int state;
	private int playerNumber;
	
	private float velX;
	private float velY;
	private boolean immortal;
	
	private int bombCapacity;
	private int firePower;
	
	public PlayerData(){
		state = B2DVars.PLAYER_DOWN;
		x = 0f;
		y = 0f;
		active = true;
		playerNumber = 1;
		velX = 0f;
		velY = 0f;
		firePower = 1;
		bombCapacity = 1;
	}
	
	public PlayerData(short type, int playerNumber, float x, float y, boolean active){
		this.type = type;
		this.x = x;
		this.y = y;
		this.active = active;
		this.playerNumber = playerNumber;
		velX = 0f;
		velY = 0f;
		bombCapacity = 1;
		firePower = 1;
	}

	public void SetVelocity(float x, float y){
		velX = x;
		velY = y;
	}
	
	public void SetState(int state){
		this.state = state;
	}
	
	public int State(){
		return state;
	}
	
	public float VelX(){
		return velX;
	}
	
	public void IncrementBombCapacity(){
		bombCapacity++;
	}
	
	public void IncrementFirePower(){
		firePower++;
	}
	
	public float VelY(){
		return velY;
	}
	
	public int PlayerNumber(){
		return playerNumber;
	}
	
	public void Kill(){
		active = false;
		state = B2DVars.PLAYER_DEAD;
	}
	
	public boolean Dead(){
		return !active;
	}
	
	public boolean Immortal(){
		return immortal;
	}
	
	public int GetFirePower(){
		return firePower;
	}
	
	public int GetBombCapacity(){
		return bombCapacity;
	}
}
