package Xbot;

import Xbot.Board.Config;

public class Stone {

	byte x, y, player;
	
	Stone(byte x, byte y, byte player){
		this.x = x;
		this.y = y;
		this.player = player;
	}
	
	boolean isBlocked(Config conf) {
		//this
		//if two stones ahead
		return true;
	}
	
	/**
	 * 
	 * @param stone
	 * @return return negative if blocked after jump
	 */
	byte canJump(Config config) {
		//hasone ahead and space
		//hastwo ahead and two space
		//hasthree ahead and three space
		return 3;
	}
	
	byte distanceToGoal(){
		//decision needed absolut or relative coords
		//switch()
		return (byte) ((byte)7-this.y); 
	}
}
