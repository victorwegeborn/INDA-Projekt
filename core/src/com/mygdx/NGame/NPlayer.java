package com.mygdx.NGame;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 *  NPlayer
 * 
 * 
 * @author victorwegeborn
 *
 */

public class NPlayer {
	
	public TextureRegion sprite;
	public int xPos;
	public int yPos;
	
	public NPlayer(TextureRegion sprite, int xPos, int yPos) {
		this.sprite = sprite;
		this.xPos = xPos;
		this.yPos = yPos;
	}
	
}
