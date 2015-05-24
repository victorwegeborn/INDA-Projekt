package com.mygdx.gameData;

import com.mygdx.game.B2DVars;

public class ItemData extends ObjectData{
	
	protected byte state;
	protected float animTimer;
	protected boolean pickedUp;
	protected byte itemType;
	
	public ItemData(){
		type = B2DVars.BIT_ITEM;
		itemType = B2DVars.BOMB_POWERUP;
		x = 0f;
		y = 0f;
		active = false;
		pickedUp = false;
	}
	
	public ItemData(byte itemType, float x, float y, float animTimer, boolean active){
		this.type = 16; // Hardcoded to B2DVars.BIT_ITEM = 16
		this.itemType = itemType;
		this.x = x;
		this.y = y;
		this.state = state;
		this.animTimer = animTimer;
		this.active = active;
		pickedUp = false;
	}
	
	public boolean PickedUp(){
		return pickedUp;
	}
	
	public void PickUp(){
		pickedUp = true;
	}
	
	public int ItemType(){
		return itemType;
	}
	
	public void SetAnimTimer(float animTimer){
		this.animTimer = animTimer;
	}
	
	public float AnimTimer(){
		return animTimer;
	}
	
}
