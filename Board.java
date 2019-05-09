package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	private Config stateConfig;

	public Board() {
		stateConfig = new Config();
	}

	public Config getStateConfig() {
		return stateConfig;
	}

	public void setStateConfig(Config stateConfig) {
		this.stateConfig = stateConfig;
	}

	/**
	 * 
	 * @param player
	 * @param conf
	 * @return
	 * @throws Exception
	 */
	public List<Move> calcFreeMoves(int player, Board board) throws Exception {
		List<Move> result = new ArrayList<Move>();

		if (board.stateConfig.stackSto[player] > 0) {
			// falls ein Stein im Stack ist f�ge start reihe hinzu, wenn nicht durch gegner
			// oder blockierten stein belegt

			for (int i = 0; i < 7; i++) {

				Move relMove = new Move(player, i, 0);
				Move absMove = relMove;
				if (player != 0) {

					absMove = KoordHelper.rotate(player, relMove);
				}
				if (spotIsFree(absMove.x, absMove.y, board.stateConfig)) {
					if (isValidMove(absMove, board)) {
						result.add(relMove);
						// System.out.println("Added Move: "+m);
					}
				}

			}
		}

		Stone[] myStones = this.getMyStones(player, board.stateConfig);
		for (Stone st : myStones) {
			/// pr�fe ob der Stein im Stack ist	
			if (!st.inStack) {
				Move move = new Move(player, st.x, st.y);
				if (isValidMove(move, board)) {
					result.add(move);
				}

			}
		}
		return result;

	}

	/**
	 * Unfinished TODO Test if correct
	 * 
	 * @param m
	 * @param board
	 * @throws Exception
	 * @returns true if possible
	 */
	public static boolean isValidMove(Move m, Board board) throws Exception {
		if ((m.x >= 0 && m.y >= 0 && m.x < 7 && m.y < 7) == false)return false;//throw new Exception("isValidMove() Borders");

		// first check if there is a stone on the coordinates
		Stone stone = board.getStoneAtKoord(m.x, m.y);
		if (stone == null) {
			// if there isn't at stone at this position check if its in the first row
			if (!board.isMoveInStartingRow(m)) {
				// if it isnt then it cant be a valid move
				return false;
			}
			// if the move is in the starting row, check if I still have stones in my stack
			if (board.stateConfig.stackSto[m.player] > 0) {
				// if I have stones in my Stack return true
				return true;
			} else {
				// otherwise no
				return false;
			}

		} else {
			// if there is a stone at this position
			if (stone.player != m.player) {
				// if the stone is not mine return false;
				return false;
			} else {

				// check if its blocked and return that
				return !stone.isBlocked(board);

			}

		}

	}

	/**
	 * 
	 * Integrate Moves into our config Hier wird ein Zug gemacht auf dem SpielBrett.
	 * Jenachdem welcher Spieler und wie viele Steine gesprungen werden sollen
	 * ver�dnert sich der Bewegungs Vektor
	 * 
	 * @param x
	 * @param y
	 * @param playerNumber
	 * @param howManyFields
	 * @param board
	 * @throws Exception moveStone() Borders
	 */
	/*
	 * Springend �ber eine beliebig lange Kette von abwechselnd nicht-eigenen
	 * Steinen und leeren Feldern. Der Zug endet entweder auf dem letzten leeren
	 * Feld der Kette oder der Stein wird vom Spielbrett entfernt, falls die Kette
	 * am Spielbrettrand mit einem nicht-eigenen Stein endet. -- wir gehen davon aus
	 * das man immer die maximale Anzahl springt // Mail von Lenz als Best�tigung
	 */
	public void moveStone(Move move) throws Exception {
		// bestimme den RichtungsVektor f�r den aktuellen Zug
			
		Vector2 moveDir = new Vector2(0, 0);
		int howManyFields = 1;
		int jmp = this.getStoneAtKoord(move.x, move.y).canJump(this);
		if (jmp > 0) {
			howManyFields = jmp * 2; // if we can jump we have to jump.
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
						if(st.x >6 || st.y > 6) { //if stones off board do what?
							st.offField= true;
							st.x = -2;
							st.y = -2;
						}
						break;
					}
				}
			}

		} 
	}

	/**
	 * hier wird der wenn vorhanden korrespondierende Stein auf dem Brett gefunden
	 * und dann nach vorne gezogen
	 * 
	 * @param move
	 * @param board
	 * @throws Exception
	 *             if player is out of range
	 */
	public void makeMove(Move move) throws Exception {
		Stone stone = null;

		if (isMoveInStartingRow(move)) {

			if (getStoneAtKoord(move.x, move.y) == null) {

				// if it isnt then take a stone from the stack and place it
				stone = getStoneFromStack(move.player);
				stone.inStack = false;
				stateConfig.stackSto[move.player]--;
				stateConfig.ptr++;

				int y = move.y;
				int x = move.x;
				stone.x = (byte) x;
				stone.y = (byte) y;
				byte player = (byte) move.player;
				stone.player = player;

			} else {

				moveStone(move);
			}

		} else {
			// if the move isnt in the first row then just make the move
			moveStone(move);
		}
	}

	private Stone getStoneFromStack(int player) {
		for (int i = player * 7; i < (player + 1) * 7; i++) {
			if (stateConfig.stones[i].inStack)
				return stateConfig.stones[i];
		}
		return null;
	}
	
	int getScore(int player) {
		int score = 0;
		for (int i = player * 7; i < (player + 1) * 7; i++) {
			if (stateConfig.stones[i].x == 7 || stateConfig.stones[i].y == 7)
				score++;
		}
		return score;
	}

	// check if move is in starting row
	public boolean isMoveInStartingRow(Move m) {
		switch (m.player) {
		case 0:
			if (m.y == 0) {
				return true;
			}
			break;
		case 1:
			if (m.x == 0) {
				return true;
			}
			break;
		case 2:
			if (m.y == 6) {
				return true;
			}
			break;
		case 3:
			if (m.x == 6) {
				return true;
			}
			break;
		}
		return false;
	}

	public Stone[] getMyStones(int player, Config conf) {
		Stone myStones[] = new Stone[7];

		// finde meine Steine
		for (int i = 0, j = 0; i < conf.stones.length; i++) {
			if (conf.stones[i].player == player) {
				myStones[j] = conf.stones[i];
				j++; // counter of found stones
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
		// respect board borders
		if ((x >= 0 && y >= 0 && x < 7 && y < 7) == false)
			return null;
		// search for stone
		for (int i = 0; i < stateConfig.stones.length; i++) { // use stateConfig.ptr for better perfomance
			if (stateConfig.stones[i].x == x && stateConfig.stones[i].y == y) {
				return stateConfig.stones[i];
			}
		}
		return null;
	}

	/**
	 * Better version of getStoneAtKoord
	 * 
	 * @param x
	 * @param y
	 * @returns null or Stone
	 */
	/*
	 * public Stone getStone(int x, int y) { // respect board borders if ((x >= 0 &&
	 * y >= 0 && x < 7 && y < 7) == false) return null; // search for stone int i =
	 * 0; while (i < stateConfig.stones.length && (stateConfig.stones[i].x != x &&
	 * stateConfig.stones[i].y != y)) { i++; } if (i != stateConfig.stones.length)
	 * return stateConfig.stones[i]; return null; }
	 */

	/**
	 * 
	 * @param x
	 * @param y
	 * @param config
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
