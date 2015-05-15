package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;

/**
 * Quantizes positions to the tilemap format
 * @author danhemgren
 */

public final class CoordinateConverter {
	
	public static Vector2 quantizePositionToGrid(Vector2 position){

		//Which tile is closest? Offsets position depending on distance to tile

		float xOffset = -0.5f;
		float yOffset = -0.5f;
		float x = Math.round(position.x + xOffset);
		float y = Math.round(position.y + yOffset);
		
		Vector2 finalpos = new Vector2(x, y);
		
		return finalpos;
	}


}
