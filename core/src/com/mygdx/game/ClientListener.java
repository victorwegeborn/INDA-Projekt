package com.mygdx.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.mygdx.game.Network.*;


public class ClientListener extends Listener {
	public void connected (Connection c) {
		
	}

	public void received (Connection c, Object o) {
		// THIS IS WHERE DATA FROM SERVER SOULD BE HANDLED LOCALY

		
	}

	public void disconnected (Connection c) {
		
	}
}
