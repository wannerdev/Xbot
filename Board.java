package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	
	class Config{
		Stone stones[] = new Stone[28]; //= new int [4][7];
		final int DIMENSION = 7;
	}
	/*
	 * pseudo code gehe nacheinander alle 7 steine durch
	 * pro steine schaue ob es die eigenen Steine sind und ob sie blockiert sind
	 * wenn frei füge zur Liste von Zügen hinzu
	 * TODO schau wie viele steine noch außerhalb des spiels
	 */
	public static List<Move> calcFreeMoves( int player, Config board){
		//Evtl ein Set nehmen?
    	List<Move> result = new ArrayList<Move>();
    	for (Stone st : board.stones) {
    		if(st.player == player && !st.isBlocked()){
    			result.add(new Move(player, st.x,st.y));
    		}
    	}
    	return result;
    }

    public static boolean isValidMove(byte x, byte y, byte player, Config board) {
    	//check if spot is empty and not in the  first row.
    	for (Stone stone: board.stones) {
    		if(x == stone.x && y == stone.y) {
    			//If not my own stone
    			if(stone.player == player) {
	    			return true;
	    		}else {
	    			return false;
	    		}
    		
    		}
    	}
    	//If first row
    	if(y == 0) {
    		return true;
    	}
    	return false;
    	
    }
    
}
