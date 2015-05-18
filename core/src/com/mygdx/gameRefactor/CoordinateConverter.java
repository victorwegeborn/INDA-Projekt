package com.mygdx.gameRefactor;

import com.badlogic.gdx.math.Vector2;

/**
 * Quantizes positions to the tilemap format
 * @author danhemgren
 */

public final class CoordinateConverter {
	
	/**
	 * Snaps current position
	 * to nearest tile center
	 * @param position
	 * @return quantized position as Vector2
	 */
	public static Vector2 quantizePositionToGrid(Vector2 position){

		float xOffset = -0.5f;
		float yOffset = -0.5f;
		float x = Math.round(position.x + xOffset);
		float y = Math.round(position.y + yOffset);
		
		Vector2 finalpos = new Vector2(x, y);
		
		return finalpos;
	}
	

	/**
	 * TODO: Tile-axis to world-axis conversion.
	 * Might be necessary to invert y-axis when 
	 * referring to tiles in world space
	 * 
	 * @param y
	 * @return
	 */
	public static int ConvertY(int y){
	
	return y;
	}


}
