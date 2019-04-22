package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	//final int DIMENSION = 7; //needed?
	Config stateConfig;
	
	class Config{
		Stone stones[] = new Stone[28]; 
		byte stackSto[]= {7,7,7,7};
		byte ptr=0; //basically a counter how many stones on board.
	}
	Board(){
		stateConfig =new Config();
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
    		if(st.player == player) {// && !st.isBlocked()){
    			result.add(new Move(player, st.x,st.y));
    		}
    	}
    	return result;
    }
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param player
	 * @param board
	 * @returns true if possible
	 */
    public static boolean isValidMove(Move m, Board board) {
    	
    	byte x, y;
    	x = (byte) m.x;
    	y = (byte) m.y;
    	//check if spot is empty and not in the  first row.
    	for (Stone stone: board.stateConfig.stones) {
	    		if(x == stone.x && y == stone.y) {
	    			//If my own stone, 
	    			//TODO and has space ahead or Jumping, In Stone ?
	    			if(stone.player == m.player ) {
	    				//Move iffree =  new Move(m.x,m.y,m.player);
	    				m.x += KoordHelper.playerToDirection((byte)m.player).x;
	    				m.y += KoordHelper.playerToDirection((byte)m.player).y;
	    				if (!stone.isBlocked(board)) {
	    					return true;
	    				}
	    				return true;//for the moment
		    		}else {
		    			//can't move someone elses stone
		    			return false;
		    		}
	    		}
    	}
		//check if stack is not empty ,
		if(board.stateConfig.stackSto[m.player] > 0){
	    	switch(m.player) {
		    	case 0:
		    		if(y == 0) {
			    		return true;
			    	}
		    		break;
		    	case 1:
		    		if(x == 0) {
		    			return true;
		    		}
		    		break;
		    	case 2:
		    		if(y == 6) {
		    			return true;
		    		}
		    		break;
		    	case 3:
		    		if(x == 6) {
		    			return true;
		    		}
		    		break;
	    	}
		}
    	return false;    	
    }
    /**
     * Integrate Moves into our config
     * @param move
     * @return
     */
    public boolean addMoveToBoard(Move move) {
    	//if stack full add stone
    	if(stateConfig.stackSto[move.player] == 7) {
    		stateConfig.stones[stateConfig.ptr] = new Stone((byte)move.x, (byte)move.y, (byte)move.player);
    		stateConfig.ptr++;
        	return true;
    	}
    	//if stone there
    	
    	Move direction = KoordHelper.playerToDirection((byte)move.player);
    	//stateConfig.stones
    	return false;
    }
    /**
     * Get potential stone 
     * @param x
     * @param y
     * @returns requested Stone or Null
     */
    public Stone getStoneAtKoord(byte x, byte y) {
    	assert x >= 0 && y >= 0;
    	for(int i=0; i< stateConfig.ptr; i++) {
    		if(stateConfig.stones[i].x == x && stateConfig.stones[i].y == y){
    			return stateConfig.stones[i];
    		}
    	}
		return null;
    }
    
    public static boolean spotIsFree(int x, int y, Board board) {
    	for (Stone stone: board.stateConfig.stones) {
    		if(x == stone.x && y == stone.y) {
    			//if stone at position
    			return false;
    		}
    	}
    	return true;
    }
}
