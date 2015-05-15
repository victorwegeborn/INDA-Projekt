package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Bomb {
	private int firePower;
	private Animation tickingAnim;
	private Animation blowUpAnim;
	private static float framerate = 1/24f; 
	
	
	public Bomb(int firePower){
		TextureAtlas spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/items/items.txt"));

		this.firePower = firePower;
		tickingAnim = new Animation(framerate, spriteSheet.findRegion("bomb"));
	
	}
	
	public int getFirePower(){
		return firePower;
	}
	
	public void setFirePower(int f){
		firePower = f;
	}
	
	

}
