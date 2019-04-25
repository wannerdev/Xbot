package Xbot;

import Xbot.Board.Config;
import lenz.htw.sawhian.Move;

public class Stone {

	//Coords absolute, decided 22.04
	byte x, y, player;
	public boolean inStack = true;
	
	Stone(byte x, byte y, byte player){
		this.x = x;
		this.y = y;
		this.player = player;
	}
	/**
	 * if two stones ahead	
	 * @param bo
	 * @return
	 */
	boolean isBlocked(Board bo) {		
		Move dir = KoordHelper.playerToDirection(player);
		return (bo.getStoneAtKoord((byte)(x+dir.x),(byte)(y+dir.y)) != null) && (bo.getStoneAtKoord((byte)(x+dir.x), (byte)(y+dir.y)) != null);
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
		//switch()
		Move dir = KoordHelper.playerToDirection(player);
		if(dir.x !=0) {
			return (byte) ((byte)7-this.x); 	
		}
		if(dir.y !=0) {
			return (byte) ((byte)7-this.y);			
		}
		return 7;
	}
}
