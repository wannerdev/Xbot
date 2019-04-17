package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	final int DIMENSION = 7; //needed
	Config config;
	
	class Config{
		Stone stones[] = new Stone[28]; //= new int [4][7];
		byte stackSto[]= new byte[4];
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
	
	/**
	 * Koords atm relative
	 * TBD relative absolute
	 * @param x
	 * @param y
	 * @param player
	 * @param board
	 * @return
	 */
    public static boolean isValidMove(Move m, Board board) {
    	byte x, y;
    	x = (byte) m.x;
    	y = (byte) m.y;
    	//check if spot is empty and not in the  first row.
    	for (Stone stone: board.config.stones) {
    		if(x == stone.x && y == stone.y) {
    			//If not my own stone
    			if(stone.player == m.player) {
	    			return true;
	    		}else {
	    			return false;
	    		}
    		
    		}
    	}
    	//If first row, 
    	if(y == 0) {
    		return true;
    	}
    	return false;
    	
    }
    
}
