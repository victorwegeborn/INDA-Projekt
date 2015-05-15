package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Quantizes body positions to the center
 * of the nearest tile
 * @author danhemgren
 */

public final class CoordinateConverter {
	
	public static Vector2 quantizePositionToGrid(Vector2 position){
	
		//Which tile is closest? Offsets position depending on distance to tile
		float xOffset = position.x % 32 < 16f ? -0.5f : 0.5f;
		float yOffset = position.y % 32 < 16f ? -0.5f : 0.5f;
		
		float x = Math.round(position.x + xOffset);
		float y = Math.round(position.y + yOffset);
		
		Vector2 finalpos = new Vector2(x, y);
		
		return finalpos;
	}

}
