package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class BombPowerUp extends Item{
	
	public BombPowerUp(World world, Vector2 poolPosition){
		super(world, poolPosition, B2DVars.BOMB_POWERUP);
		body.setUserData(this); //store this object for reference in body
		TextureAtlas spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/items/powerups.txt"));
		animation = new Animation(1/12f, spriteSheet.findRegions("bombpowerup"));
		animation.setPlayMode(PlayMode.LOOP);
	}

}
