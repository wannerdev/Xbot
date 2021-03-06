package Xbot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lenz.htw.sawhian.Move;

public class Board {
	private Config stateConfig;
	private Map<Float,Config> confs;
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
			// falls ein Stein im Stack ist f�ge start reihe hinzu, wenn nicht durch
			// gegner
			// oder blockierten stein belegt

			for (int i = 0; i < 7; i++) {

				Move relMove = new Move(player, i, 0); // unrotated
				Move absMove = new Move(player, i, 0);
				if (player != 0) {
					Move cache = KoordHelper.rotate(player, relMove);
					int x = cache.x;
					int y = cache.y;
					int pl = cache.player;
					absMove = new Move(pl, x, y); // rotated
				}
				// If not player 0 absMove is relativemove
				if (spotIsFree(absMove.x, absMove.y, board.stateConfig)) {
					if (isValidMove(absMove, board)) {
						result.add(absMove);
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
	 * 
	 * 
	 * @param m
	 * @param board
	 * @throws Exception
	 * @returns true if possible
	 */
	public static boolean isValidMove(Move m, Board board) throws Exception {
		if (m == null) {
			throw new Exception("NO Move\n"+board.stateConfig);
			// return;
		}
		if ((m.x >= 0 && m.y >= 0 && m.x < 7 && m.y < 7) == false)
			return false;// throw new Exception("isValidMove() Borders");

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
	 * @throws Exception
	 *             moveStone() Borders
	 */
	/*
	 * Springend �ber eine beliebig lange Kette von abwechselnd nicht-eigenen
	 * Steinen und leeren Feldern. Der Zug endet entweder auf dem letzten leeren
	 * Feld der Kette oder der Stein wird vom Spielbrett entfernt, falls die Kette
	 * am Spielbrettrand mit einem nicht-eigenen Stein endet. -- wir gehen davon aus
	 * das man immer die maximale Anzahl springt // Mail von Lenz als Best�tigung
	 */
	public boolean moveStone(Move move) throws Exception {
		// bestimme den RichtungsVektor f�r den aktuellen Zug
		if (move == null) {
			throw new Exception("NO Move\n"+this.stateConfig);
			// return;
		}
		Vector2 moveDir = new Vector2(0, 0);
		int howManyFields = 1;
		Stone movingStone = this.getStoneAtKoord(move.x, move.y);
		if (movingStone == null) {
			// System.err.println("GameOver"+this.getScores());
			return false;
		}
		int jmp = movingStone.canJump(this);
		if (jmp > 0) {
			howManyFields = jmp * 2; // if we can jump we have to jump.
			movingStone.hasJumped = jmp;
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
						if (st.x > 6 || st.y > 6) { // if stones off board set flag that it is scored
							st.isScored = true;
							st.x = -2;
							st.y = -2;
							stateConfig.ptr--;
						}
						break;
					}
				}
			}

		}
		return true;
	}

	/**
	 * hier wird der wenn vorhanden korrespondierende Stein auf dem Brett gefunden
	 * und dann nach vorne gezogen
	 * 
	 * @param move
	 * @param board
	 * @throws Exception if Move null
	 *             
	 */
	public boolean makeMove(Move move) throws Exception {
		boolean result=false;
		if (move == null) {
			// System.err.println("NO Move");
			throw new Exception("NO Move\n"+this.stateConfig);
			// return;
		}
		Stone stone = null;

		if (isMoveInStartingRow(move)) {
			if (getStoneAtKoord(move.x, move.y) == null) {
				// if it isn't then take a stone from the stack and place it
				stone = getStoneFromStack(move.player);
				if (stone == null) {
					throw new Exception("Probably something with the board representation, no stone in stack");
				}
				// if we have a stone in stack
				stone.inStack = false;
				stateConfig.stackSto[move.player]--;
				stateConfig.ptr++;

				int y = move.y;
				int x = move.x;
				stone.x = (byte) x;
				stone.y = (byte) y;
				byte player = (byte) move.player;
				stone.player = player;
				result=true;
			} else {
				result = moveStone(move);
			}

		} else {
			// if the move isn't in the first row then just make the move
			result = moveStone(move);
		}
		return result;
	}

	private Stone getStoneFromStack(int player) {
		for (int i = player * 7; i < ((player + 1) * 7); i++) {
			if (stateConfig.stones[i].inStack)
				return stateConfig.stones[i];
		}
		// System.err.println("getStoneFromStack player"+player+"\n config:\n
		// "+stateConfig.toString());
		return null;
	}

	int getScore(int player) {
		int score = 0;
		for (int i = player * 7; i < ((player + 1) * 7); i++) {
			if (stateConfig.stones[i].isScored)
				score++;
		}
		return score;
	}

	boolean isAllBlocked(int player, Board board) {
		int blocked = 0;
		for (int i = player * 7; i < ((player + 1) * 7); i++) {
			if (stateConfig.stones[i].isBlocked(board))
				blocked++;
		}

		return (blocked == 7);
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
		for (int i = 0; i < stateConfig.stones.length; i++) {
			if (stateConfig.stones[i].x == x && stateConfig.stones[i].y == y) {
				return stateConfig.stones[i];
			}
		}
		return null;
	}

	public String getScores() {
		return "P0: " + this.getScore(0) + " P1: " + this.getScore(1) + " P2: " + this.getScore(2) + " P3: "
				+ this.getScore(3);
	}

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
