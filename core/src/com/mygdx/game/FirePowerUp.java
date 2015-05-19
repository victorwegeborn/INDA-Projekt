package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gameData.ItemData;

public class FirePowerUp extends Item{
	
	
	public FirePowerUp(World world, Vector2 poolPosition) {
		super(world, poolPosition, B2DVars.FIRE_POWERUP);
		
		TextureAtlas spriteSheet = new TextureAtlas(Gdx.files.internal("sprites/items/powerups.txt"));
		animation = new Animation(1/12f, spriteSheet.findRegions("firepowerup"));
		animation.setPlayMode(PlayMode.LOOP);	
		data = new ItemData(B2DVars.FIRE_POWERUP, body.getPosition().x, body.getPosition().y, 
				animTimer, false);
		
		body.setUserData(data); //store this object for reference in body

	}
	
	
}
