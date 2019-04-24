package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	// final int DIMENSION = 7; //needed?
	Config stateConfig;

	class Config {
		Stone stones[] = new Stone[28];
		byte stackSto[] = { 7, 7, 7, 7 };
		byte ptr = 0; // basically a counter how many stones on board.
	}

	Board() {
		stateConfig = new Config();
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
		stateConfig = new Config();
		stateConfig.stones = new Stone[28];
		

		
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

	public static Move bestMove(int playerNumber, Board b) {

		List<Move> myPossibleMoves = calcFreeMoves(playerNumber, b);
		//System.out.println(myPossibleMoves.size());
		if (myPossibleMoves.size() > 0) {

		    int moveIndex = (int) (Math.random() * myPossibleMoves.size());
			Move myMove = myPossibleMoves.get(moveIndex);
			return myMove;

		} else {
			return null;
		}

	}

	/*
	 * TODO: STONE SET UP RICHTIG MACHEN UND HERAUS FINDEN WIE DIE MOVES
	 * FUNKTIONIEREN!!!
	 */
	public static List<Move> calcFreeMoves(int player, Board board) {
		// Evtl ein Set nehmen?
		List<Move> result = new ArrayList<Move>();
		Stone[] myStones = getMyStones(player, board);
		for (Stone st : myStones) {
			/// pr�fe ob der Stein im Stack ist
			if (!st.inStack) {
				/*wenn nicht und er nicht blokiert ist, dann f�ge ihn zu den M�glichen Z�gen
				Move move = new Move(player, st.x, st.y);
				if (isValidMove(move, board)) {
					result.add(move);
				}*/

			} else if (st.inStack) {
				// falls er im Stack ist, perform set up je nachdem welcher spieler dran ist
				Vector2 vec = new Vector2 (0,0);
				vec = new Vector2(7-board.stateConfig.stackSto[player], 0);
			

				Move move = new Move(player, vec.x, vec.y);
				if (spotIsFree(move.x,move.y, board)) {
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
		// check if spot is empty and not in the first row.
		for (Stone stone : board.stateConfig.stones) {
			if (x == stone.x && y == stone.y) {
				// If my own stone,
				// TODO and has space ahead or Jumping, In Stone ?
				if (stone.player == m.player) {
					// Move iffree = new Move(m.x,m.y,m.player);
					m.x += KoordHelper.playerToDirection((byte) m.player).x;
					m.y += KoordHelper.playerToDirection((byte) m.player).y;
					if (!stone.isBlocked(board)) {
						return true;
					}
					return true;// for the moment
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
	 * Integrate Moves into our config
	 * 
	 * @param move
	 * @return
	 */

	/*
	 * Hier wird ein Zug gemacht auf dem SpielBrett. Jenachdem welcher Spieler und
	 * wie viele Steine gesprungen werden sollen ver�dnert sich der Bewegungs Vektor
	 */
	public void moveStone(int x, int y, int playerNumber, int howManyFields, Board board) {

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

		} else {

			for (Stone st : board.stateConfig.stones) {

				if (st.inStack && st.player == playerNumber) {

					st.x = (byte) x;
					st.y = (byte) y;
					st.inStack = false;
					board.stateConfig.stackSto[playerNumber]--;
					return;
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
			moveStone(move.x, move.y, PlayerNumber, 1, board);

		}
	}

	public static Stone[] getMyStones(int player, Board board) {
		Stone myStones[] = new Stone[7];
		int indexer= 0;
		// finde meine Steine
		for (int i = 0; i < board.stateConfig.stones.length;i++) {

			if (board.stateConfig.stones[i].player == player) {
				myStones[indexer] = board.stateConfig.stones[i];
				indexer++;
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
	public Stone getStoneAtKoord(byte x, byte y) {
		assert x >= 0 && y >= 0;
		for (int i = 0; i < stateConfig.ptr; i++) {
			if (stateConfig.stones[i].x == x && stateConfig.stones[i].y == y) {
				return stateConfig.stones[i];
			}
		}
		return null;
	}

	public static boolean spotIsFree(int x, int y, Board board) {
		for (Stone stone : board.stateConfig.stones) {
			if (x == stone.x && y == stone.y) {
				// if stone at position
				return false;
			}
		}
		return true;
	}
}
