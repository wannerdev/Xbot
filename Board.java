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

	public List<Move> calcFreeMoves(int player, Config conf) throws Exception {
		// Evtl ein Set nehmen?
		List<Move> result = new ArrayList<Move>();
		Stone[] myStones = this.getMyStones(player, conf);
		for (Stone st : myStones) {
			/// pr�fe ob der Stein im Stack ist
			if (!st.inStack) {
				/*wenn nicht und er nicht blokiert ist, dann f�ge ihn zu den M�glichen Z�gen
				Move move = new Move(player, st.x, st.y);
				if (isValidMove(move, board)) {
					result.add(move);
				}*/

			}
		}
		//TODO FIXME not working properly
		//Add first row as possible moves
		if(conf.stackSto[player] != 0) {
			// falls ein Stein im Stack ist f�ge start reihe hinzu
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
	---
	/**
	 * Unfinished
	 * TODO finish
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
	 *  wie viele Steine gesprungen werden sollen ver�dnert sich der Bewegungs Vektor
	 * @param x
	 * @param y
	 * @param playerNumber
	 * @param howManyFields
	 * @param board
	 */
	/*
	Springend �ber eine beliebig lange Kette von abwechselnd nicht-eigenen Steinen und leeren Feldern.
	Der Zug endet entweder auf dem letzten leeren Feld der Kette oder der Stein wird vom Spielbrett entfernt,
	falls die Kette am Spielbrettrand mit einem nicht-eigenen Stein endet.
	--ich denke das hei�t wir gehen davon aus das man immer die maximale Anzahl springt da wir ja nur koordinaten vom Server bekommen.
	*/
	public void moveStone(Move move ) {
		// bestimme den RichtungsVektor f�r den aktuellen Zug
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
	 * hier wird der Korrespondierende Stein auf dem Brett gefunden und dann nach
	 * vorne gezogen TODO: Jump modifier einbauen
	 * 
	 * @param move
	 * @param board
	 */
	public void makeMove(Move move) {
		Stone stone =null;
		if (stateConfig.stackSto[move.player] == 0) {
			// Falls keine Steine mehr im Stack
			moveStone(move);
		}else if(stateConfig.stackSto[move.player] == 7){
			//Stein wird aufjedenfall aus dem Stack platziert
			stone = stateConfig.stones[(stateConfig.stackSto[move.player]*move.player+1)-1]; 
			//get the index through the fixed index in the konstruktor
			assert (stone.player == move.player);
			if(stone != null ) {
				stateConfig.ptr++;
				stone.inStack = false;
				stone.x = (byte) move.x;
				stone.y = (byte) move.y;
			}
		}else {
			stone = stateConfig.stones[(stateConfig.stackSto[move.player]*move.player+1)-1];
			if(getStone(move.x, move.y)!=null) {
				moveStone(move);				
			}
			//check if move is to start row if yes get the stone and do the necessary stuff
			stateConfig.ptr++;
			stone.inStack = false;
			stone.x = (byte) move.x;
			stone.y = (byte) move.y;
			moveStone(move);
		}
	}

	
	//TODO write function
	//check if move is starting row
	
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

	public boolean addMoveToBoard(Move move) {
		// if stack full add stone
		if (stateConfig.stackSto[move.player] == 7) {
			stateConfig.stones[stateConfig.ptr] = new Stone((byte) move.x, (byte) move.y, (byte) move.player);
			stateConfig.ptr++;
			return true;
		}
		// if stone there

		Move direction = KoordHelper.playerToDirection((byte) move.player);
		// stateConfig.stones
		return false;
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
	 * 
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
