package com.mygdx.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;


public class ServerListener extends Listener{
	
	public void connected (Connection c) {
		Log.info("[SERVER] client connecting");
	}

	
	public void disconnected (Connection c) {
		Log.info("[SERVER] client disconnecting");
	}

	
	public void received (Connection c, Object o) {
		// HERE GOES ALL INGAME LOGIC FOR SERVER TO PROCESS
	}
	
}
