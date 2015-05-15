package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

public final class CoordinateConverter {
	
	public static Vector2 quantizePositionToGrid(Vector2 position){
		//TODO: (x,y)-coordinates to grid coordinate conversion
		
		float x = position.x;
		float y = position.y;
		
		//Which tile is this?
//		x = x / 32;
//		y = y / 32;
		float xOffset = x % 32 < 16f ? -0.5f : 0.5f;
		float yOffset = y % 32 < 16f ? -0.5f : 0.5f;
		
		x = Math.round(x + xOffset);
		y = Math.round(y + yOffset);
		
		Vector2 finalpos = new Vector2(x, y);
		
		return finalpos;
	}

}
