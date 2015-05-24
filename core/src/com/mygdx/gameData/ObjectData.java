package com.mygdx.gameData;

import com.mygdx.gameData.PlayerData;

/**
 * Container class used to send
 * game object information over 
 * tcp packets 
 * @author danhemgren
 *
 */

public abstract class ObjectData {

	//Coordinates in game world
	protected float x;
	protected float y;
	
	//If object is active
	protected boolean active;
	
	//Flag object for reset
	protected boolean reset;
	
	//Collision layer for this object
	protected byte type;
	
	public ObjectData(){
		x = 0f;
		y = 0f;
		active = false;
		reset = false;
		type = 0;
	}
	
	public float PosX(){
		return x;
	}
	
	public float PosY(){
		return y;
	}
	
	public boolean Active(){
		return active;
	}
	
	public short GetType(){
		return type;
	}
	
	public void SetPosition(float x, float y){
		this.x = x;
		this.y = y;
	}
	
	
	public void SetActive(boolean active){
		this.active = active;
	}
	
	public void FlagReset(){
		reset = true;
	}
	
	public void UnflagReset(){
		reset = false;
	}
	
	public boolean ResetFlagged(){
		return reset;
	}

}
