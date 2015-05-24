package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.gameData.BoxData;

public class Box {
	
	public Body body;
	private BoxData data;
	
	public boolean active;
	
	public Box(Body body){
		this.body = body;
		data = new BoxData((byte)B2DVars.BIT_BOX, body.getPosition().x, body.getPosition().y, true);
		body.setUserData(data);
	}
	
	public void Update(float dt){
		active = body.isActive();
		UpdateBoxData();
	}

	private void UpdateBoxData(){
		data.SetPosition(body.getPosition().x, body.getPosition().y);
		data.SetActive(active);
	}
	
	public boolean IsActive(){
		return active;
	}
}
