package Xbot;

import lenz.htw.sawhian.Move;

public class KoordHelper {

	public static Move rotate(byte player, Move move) {
		//System.out.println("Before turn"+amount+" X:"+move.x +" y:"+ move.y);
		for(int i=0; i< player; i++) {
			int y = move.y;
			int x = (move.x-6)*-1;
			move.x = y;
			move.y = x;
		}
		//System.out.println("After X:"+move.x +" y:"+ move.y);
		return move;
	}
	
	public static Move playertoDir(byte player) {
		assert player <=3 && player > -1;
		switch(player) {
	    	case 0:
		    	return new Move(0,1,player);
	    	case 1:
	    		return new Move(1,0,player);
	    	case 2:
	    		return new Move(0,-1,player);
	    	case 3:
	    		return new Move(-1,0,player);
		}
		return null;
	}
}
