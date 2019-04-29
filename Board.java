package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	Config stateConfig;

	class Config {
		Stone stones[] = new Stone[28];
		byte stackSto[] = { 7, 7, 7, 7 };
		byte ptr = 0; // basically a counter how many stones on board.
	}

	// set up the stones and assign them a player number
	public Board() {
		stateConfig = new Config();
		
		for (int i = 0; i < stateConfig.stones.length; i++) {
			stateConfig.stones[i] = new Stone ((byte)-1,(byte)-1,(byte)0);
			stateConfig.stones[i].inStack = true;
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

	/**
	 * 
	 * @param player
	 * @param conf
	 * @return
	 * @throws Exception
	 */
	public List<Move> calcFreeMoves(int player, Config conf) throws Exception {
		// Evtl ein Set nehmen?
		List<Move> result = new ArrayList<Move>();
		Stone[] myStones = this.getMyStones(player, conf);
		for (Stone st : myStones) {
			/// prüfe ob der Stein im Stack ist
			if (!st.inStack && !st.isBlocked(this)) {
				//wenn nicht im stack und er nicht blokiert ist, dann fï¿½ge ihn zu den Mï¿½glichen Zï¿½gen
				if(st.x > 6 && st.y > 6) {
					throw new Exception("bounds");
				}
				Move move = new Move(player, st.x, st.y);
				if (isValidMove(move, this)) {
					result.add(move);
				}

			}
		}
		//TODO FIXME not working properly
		//Add first row as possible moves
		if(conf.stackSto[player] != 0) {
			// falls ein Stein im Stack ist fï¿½ge start reihe hinzu
			Move dir = KoordHelper.playerToDirection(player);
			Move addTo = new Move(player,0, 0 );
			addTo.x += dir.x;
			addTo.y += dir.y;
			//Move move = new Move(player, vec.x, vec.y);
			for(int i = 0; i < 7; i++) {
				if (spotIsFree(i, 0, conf)) {
					Move move = new Move(player,i, 0 );
					result.add(move);
				}
			}
		}
		return result;
	}

/*Test what does the server do in
	
	-x
	-
	---*/
	/**
	 * Unfinished
	 * TODO Test if correct
	 * @param m
	 * @param board
	 * @returns true if possible
	 */
	public static boolean isValidMove(Move m, Board board) {

		byte x, y;
		x = (byte) m.x;
		y = (byte) m.y;
		// check if spot is empty and not in the first row.
		for (Stone stone : board.stateConfig.stones) {
			if (x == stone.x && y == stone.y) {
				// If my own stone
				if (stone.player == m.player) {
					if (!stone.isBlocked(board) || stone.canJump(board)>0) {
						return true;
					}
				} else {
					// can't move someone elses stone
					return false;
				}
			}
		}
		// check if stack is not empty ,
		if (board.stateConfig.stackSto[m.player] > 0) {
			switch (m.player) {
			case 0:
				if (y == 0) {
					return true;
				}
				break;
			case 1:
				if (x == 0) {
					return true;
				}
				break;
			case 2:
				if (y == 6) {
					return true;
				}
				break;
			case 3:
				if (x == 6) {
					return true;
				}
				break;
			}
		}
		return false;
	}

	/**
	 * 
	 *  Integrate Moves into our config
	 *  Hier wird ein Zug gemacht auf dem SpielBrett. Jenachdem welcher Spieler und
	 *  wie viele Steine gesprungen werden sollen verï¿½dnert sich der Bewegungs Vektor
	 * @param x
	 * @param y
	 * @param playerNumber
	 * @param howManyFields
	 * @param board
	 */
	/*
	Springend ï¿½ber eine beliebig lange Kette von abwechselnd nicht-eigenen Steinen und leeren Feldern.
	Der Zug endet entweder auf dem letzten leeren Feld der Kette oder der Stein wird vom Spielbrett entfernt,
	falls die Kette am Spielbrettrand mit einem nicht-eigenen Stein endet.
	-- wir gehen davon aus das man immer die maximale Anzahl springt // Mail von Lenz als Bestätigung
	*/
	public void moveStone(Move move ) {
		// bestimme den RichtungsVektor fï¿½r den aktuellen Zug
		Vector2 moveDir = new Vector2(0, 0);
		int howManyFields = 1;
		int jmp = this.getStone(move.x, move.y).canJump(this);
		if(jmp > 0) {
			howManyFields = jmp*2; //if we can jump we have to jump.
		}
		int x = move.x;
		int y = move.y;
		
		switch (move.player) {
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

		} else {
			for (Stone st : stateConfig.stones) {
				if (st.inStack && st.player == move.player) {
					st.x = (byte) x;
					st.y = (byte) y;
					st.inStack = false;
					stateConfig.stackSto[move.player]--;
					stateConfig.ptr++;
					return;
				}
			}
		}
	}

	/**
	 * hier wird der wenn vorhanden korrespondierende Stein auf dem Brett gefunden und dann nach
	 * vorne gezogen 
	 * 
	 * @param move
	 * @param board
	 * @throws Exception if player is out of range
	 */
	public void makeMove(Move move) throws Exception {
		Stone stone=null;
		if (stateConfig.stackSto[move.player] == 0 || getStone(move.x, move.y) != null) {
			// Falls keine Steine mehr im Stack oder stein schon auf dem Brett
			moveStone(move);
		}else if(stateConfig.stackSto[move.player] == 7 && isMoveInStartingRow(move)){
			//Stein wird aufjedenfall aus dem Stack platziert
			//get a stone from stack
			stone = stateConfig.stones[7*move.player];

			assert (stone.player == move.player);
			stateConfig.ptr++;
			stone.inStack = false;
			
			stateConfig.stackSto[move.player]--;
			
			stone.x = (byte) move.x;
			stone.y = (byte) move.y;
			stone.player =(byte) move.player;
			
		}else  {
			//check if move is to start row if yes place the stone and do the necessary stuff
			if (isMoveInStartingRow(move)) {
				stone = getStoneFromStack(move.player);
				stateConfig.ptr++;
				stone.inStack = false;
				stateConfig.stackSto[move.player]--;
				
				stone.x = (byte) move.x;
				stone.y = (byte) move.y;
				stone.player =(byte) move.player;
			}
		}
	}

	
	private Stone getStoneFromStack(int player) {
		for(int i = player*7; i< (player+1)*7; i++) {
			if(stateConfig.stones[i].inStack)return stateConfig.stones[i];
		}
		return null;	
	}

	//check if move is in starting row	
	public boolean isMoveInStartingRow(Move move) throws Exception {
		
		switch (move.player) {
				case 0:
					return !(move.y > 0);
					
				case 1:
					return !(move.x > 0);
					
				case 2:
					return !(move.x < 5);
					
				case 3:
					return !(move.y < 5);
		}
		throw new Exception("Player invalid");
	}
	
	public Stone[] getMyStones(int player, Config conf) {
		Stone myStones[] = new Stone[7];
		
		// finde meine Steine
		for (int i = 0,j= 0; i < conf.stones.length;i++) {
			if (conf.stones[i].player == player) {
				myStones[j] = conf.stones[i];
				j++; //counter of found stones
			}
		}

		return myStones;
	}


	/**
	 * Get potential stone
	 * 
	 * @param x
	 * @param y
	 * @returns requested Stone or Null
	 */
	public Stone getStoneAtKoord(int x, int y) {
		//respect board borders
		if( (x >= 0 && y >= 0 && x <8 && y<8)==false)return null;
		//search for stone		
		for (int i = 0; i < stateConfig.stones.length; i++) { //use stateConfig.ptr for better perfomance
			if (stateConfig.stones[i].x == x && stateConfig.stones[i].y == y) {
				return stateConfig.stones[i];
			}
		}
		return null;
	}
	
	/**
	 * Better version of getStoneAtKoord
	 * @param x
	 * @param y
	 * @returns null or Stone
	 */
	public Stone getStone(int x, int y) {
		//respect board borders
		if( (x >= 0 && y >= 0 && x <8 && y<8)==false)return null;
		//search for stone
		int i=0;
		while(i<stateConfig.stones.length && (stateConfig.stones[i].x != x && stateConfig.stones[i].y != y)) {
			i++;
		}
		if(i != stateConfig.stones.length)return stateConfig.stones[i];
		return null;
	}
		

	/**
	 * 
	 * @param x
	 * @param y
	 * @param board
	 * @returns true if spot is free
	 */
	public static boolean spotIsFree(int x, int y, Config conf) {
		for (Stone stone : conf.stones) {
			if (x == stone.x && y == stone.y) {
				// if stone at position
				return false;
			}
		}
		return true;
	}
}
