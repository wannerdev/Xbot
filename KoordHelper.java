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
	
	public static Move rotate(int player, Move move) {
		return rotate((byte)player, move);
	}
	
	public static Move playerToDirection(int player) {
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
	
	
	/**
	 * 
	/**Generat absolute start row koords
	 * IF wanted FIX
	 * 
	 * @return
	 */
	public static Move GeneratAbsolutestartrow (){
		Move m=new Move(0,0,0);
		for(int i = 0; i < 7; i++) {
			switch (m.player) {
				case 0:
					m.y = 0;
					m.x = i;
					break;
				case 1:
					m.x = 0;
					m.y = i;
					break;
				case 2:
					m.y = 6;
					m.x = i;
					break;
				case 3:
					m.x = 6;
					m.y = i;
					break;
			}
			//if (spotIsFree(m.x, m.y, conf)) {
				//if (isValidMove(m, this)) {
					//result.add(m);
				//}
			///}
		}
		return m;
	}
}
