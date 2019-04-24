package Xbot;

import java.util.ArrayList;
import java.util.List;

import lenz.htw.sawhian.Move;

public class Board {
	final int DIMENSION = 7; // needed
	Config config;

	class Config {
		Stone stones[] = new Stone[28]; // = new int [4][7];
		byte stackSto[] = new byte[4];

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

		for (int i = 0; i < config.stones.length; i++) {

			if (i < 7) {
				config.stones[i].player = 0;

			} else if (i >= 7 && i < 14) {
				config.stones[i].player = 1;

			} else if (i >= 14 && i < 21) {
				config.stones[i].player = 2;

			} else if (i >= 21 && i < 28) {
				config.stones[i].player = 3;

			}
		}

	}

	/*
	 * Berechne die spielbaren Züge für eínen spezifischen Spieler in einer
	 * spezifischen Brett Konfiguration Dazu werden alle Steine gesucht, die nicht
	 * blockiert sind oder im stack sind. Deren koordinaten werden dann als
	 * möglicher Move in eine Liste gespeichert
	 */

	public static List<Move> calcFreeMoves(int player, Config board) {

		List<Move> result = new ArrayList<Move>();
		List<Stone> myStones = new ArrayList<Stone>();
		// finde meine Steine
		for (Stone st : board.stones) {

			if (st.player == player) {
				myStones.add(st);
			}

		}

		for (Stone st : myStones) {
			/// prüfe ob der Stein im Stack ist
			if (!st.inStack && !st.isBlocked()) {
				// wenn nicht und er nicht blokiert ist, dann füge ihn zu den Möglichen Zügen
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
				// füge den Zug hinzu
				result.add(new Move(player, vec.x, vec.y));
			}

		}
		return result;
	}

	void setUpStone(int x, int y) {

		for (Stone st : config.stones) {

			if (st.inStack) {
			   st.x = (byte) x; 
			   st.y = (byte) y;
			   st.inStack = false;
			}

		}

	}

	/*
	 * Hier wird ein Zug gemacht auf dem SpielBrett. Jenachdem welcher Spieler und
	 * wie viele Steine gesprungen werden sollen verädnert sich der Bewegungs Vektor
	 */
	void moveStone(int x, int y, int playerNumber, int howManyFields) {

		// bestimme den RichtungsVektor für den aktuellen Zug
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

		// suche durch alle existierenden Steine durch
		for (Stone st : config.stones) {

			if (x == st.x) {
				if (y == st.y) {
					// wenn wir den richtigen Stein haben, mach den Zug mit dem Stein

					st.x += moveDir.x;
					st.y += moveDir.y;

					break;
				}
			}
		}

	}

	/*
	 * hier wird der Korrespondierende Stein auf dem Brett gefunden und dann nach
	 * vorne gezogen TODO: Jump modifier einbauen,
	 * 
	 */
	public void makeMove(Move move, int PlayerNumber, int howManyFields) {

		// suche durch alle existierenden Steine durch
		for (Stone st : config.stones) {

			if (move.x == st.x) {
				if (move.y == st.y) {

					if (!st.inStack) {

						moveStone(move.x, move.y, PlayerNumber, howManyFields);

					} else {

						setUpStone(move.x, move.y);

					}

				}

			}
		}

	}

	public static Move bestMove(int playerNumber, Board b) {

		Move myMove = new Move(playerNumber, 0, 0);

		/*
		 * List<Stone> myStones = new ArrayList<Stone>();
		 * 
		 * 
		 * find my Stones on the board and add them to my List
		 * 
		 * 
		 * for (Stone st : b.config.stones) {
		 * 
		 * if (st.player == playerNumber) {
		 * 
		 * myStones.add(st);
		 * 
		 * }
		 * 
		 * int stoneIndex = 0;
		 */
		/// now get all the free possible moves for the correspoding player
		List<Move> myPossibleMoves = calcFreeMoves(playerNumber, b.config);

		if (myPossibleMoves.size() > 0) {

			int moveIndex = (int) (Math.random() * myPossibleMoves.size());
			myMove = myPossibleMoves.get(moveIndex);

		} else {

		}

		return myMove;

	}

	/**
	 * Koords atm relative TBD relative absolute
	 * 
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

		// check if spot is empty and not in the first row.
		for (Stone stone : board.config.stones) {
			if (x == stone.x && y == stone.y) {
				// If not my own stone
				if (stone.player == m.player) {
					return true;
				} else {
					return false;
				}

			}
		}
		// If first row,
		if (y == 0) {
			return true;
		}
		return false;

	}

}
