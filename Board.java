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
	
	
	
	static class Vector2 {

		int x;
		int y;

		public Vector2(int x, int y) {

			this.x = x;
			this.y = y;

		}

	}
	
	
	/*
	 * TODO: STONE SET UP RICHTIG MACHEN UND HERAUS FINDEN WIE DIE MOVES
	 * FUNKTIONIEREN!!!
	 * 
	 * 
	 */

	// set up the stones and assign them a player number
	public void init() {

		for (int i = 0; i < stateConfig.stones.length; i++) {

			if (i < 7) {
				stateConfig.stones[i].player = 0;

			} else if (i >= 7 && i < 14) {
				stateConfig.stones[i].player = 1;

			} else if (i >= 14 && i < 21) {
				stateConfig.stones[i].player = 2;

			} else if (i >= 21 && i < 28) {
				stateConfig.stones[i].player = 3;

			}
		}

	}

	
	
	/*
	 * TODO: STONE SET UP RICHTIG MACHEN UND HERAUS FINDEN WIE DIE MOVES
	 * FUNKTIONIEREN!!!	 
	 */
	public static List<Move> calcFreeMoves( int player, Board board){
		//Evtl ein Set nehmen?
    	List<Move> result = new ArrayList<Move>();
    	List<Stone> myStones = getMyStones(player,board);
    	for (Stone st : myStones) {
			/// pr�fe ob der Stein im Stack ist
			if (!st.inStack && !st.isBlocked(board)) {
				// wenn nicht und er nicht blokiert ist, dann f�ge ihn zu den M�glichen Z�gen
				// hinzu Frage: stimmt das? muss hier nicht die pos von dem stein plus die position nach dem zug hin??
				result.add(new Move(player, st.x, st.y));
			} else if (st.inStack) {
				// falls er im Stack ist, perform set up je nachdem welcher spieler dran ist
				
				
				Vector2 vec = new Vector2(0, 0);
				switch (player) {

				case 0:
					// spieler 1 die komplette erste x reihe von links nach rechts
					vec = new Vector2(myStones.lastIndexOf(st), 0);
					break;
				case 1:
					// spieler 2 die komplette erste y reihe von oben nach unten
					vec = new Vector2(0, 7 - myStones.lastIndexOf(st));
					break;
				case 2:
					// spieler 3 die komplette obereste x reihe, von rechts nach links
					vec = new Vector2(7 - myStones.lastIndexOf(st), 7);
					break;
				case 3:
					// spieler 4 die letze y reihe, von unten nach oben
					vec = new Vector2(7, myStones.lastIndexOf(st));
					break;

				}
				// f�ge den Zug hinzu
				
				Move move = new Move(player, vec.x, vec.y);
				if (isValidMove(move,board)) {
					result.add(move);				
				}
		
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
    
	/*
	 * Hier wird ein Zug gemacht auf dem SpielBrett. Jenachdem welcher Spieler und
	 * wie viele Steine gesprungen werden sollen ver�dnert sich der Bewegungs Vektor
	 */
	void moveStone(int x, int y, int playerNumber, int howManyFields, Board board) {

		// bestimme den RichtungsVektor f�r den aktuellen Zug
		Vector2 moveDir = new Vector2(0, 0);

		switch (playerNumber) {
		case 0:

			moveDir.y = 1 * howManyFields;
			break;
		case 1:

			moveDir.x = 1 * howManyFields;
			break;
		case 2:

			moveDir.y = -1 * howManyFields;
			break;
		case 3:

			moveDir.x = -1 * howManyFields;
			break;
		}
        
		if (howManyFields != 0) {
			// suche durch alle existierenden Steine durch
			for (Stone st : stateConfig.stones) {
				if (x == st.x) {
					if (y == st.y) {
						// wenn wir den richtigen Stein haben, mach den Zug mit dem Stein

						st.x += moveDir.x;
						st.y += moveDir.y;
						break;
					}
				}
			}
			
		}else {
			
			List<Stone> myStones = getMyStones(playerNumber,board);
			
			for (Stone st : myStones) {
				
				if (st.inStack) {
					
				  st.x = (byte)x;
				  st.y = (byte) y;
				}
			}			
		}		
	}
    
    /*
	 * hier wird der Korrespondierende Stein auf dem Brett gefunden und dann nach
	 * vorne gezogen TODO: Jump modifier einbauen,
	 * 
	 */
	public void makeMove(Move move, int PlayerNumber, Board board) {

		if (stateConfig.stackSto[PlayerNumber] == 0) {
			
			// Fall keine Steine mehr im Stack
			moveStone(move.x, move.y, PlayerNumber, 1,board);
			
			}	
	}
	
	
	public static List<Stone> getMyStones(int player, Board board){
		List<Stone> myStones = new ArrayList<Stone>();
		// finde meine Steine
		for (Stone st : board.stateConfig.stones) {

			if (st.player == player) {
				myStones.add(st);
			}

		}

		
		
		return myStones;
		
	}
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
