package learn.rl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TicTacToe1 {
	float[] scoreTable = new float[19683];
	int[] board = new int[9];
	public boolean turnX = true;
	float alpha = 0.05f;

	public static void main(String[] args) {
		// t.printScoreMap();
		TicTacToe1 t = new TicTacToe1();
		t.train();
		System.out.println("Play: (input 1 to 9)");
		t.play();
	}
	
	public void train() {
		System.out.println("training AI player 2...");
		for (int i = 0; i < 50000; i++) {
			clearBoard();
			int pos = -1;
			do {
				pos = stepNext();
				// t.printBoard();
				// System.out.println();
				int index = getBoardIndex();
				int winer = calWiner();
				if (winer > 0) {
					scoreTable[index] = (winer == 2) ? 1.0f : -1.0f;
					break;
				} else {
					updateScoreMap(index);
				}
			} while (pos >= 0);
		}
	}

	public void printScoreMap() {
		for (int i = 0; i < scoreTable.length; i++) {
			if (scoreTable[i] != 0)
				System.out.println("idx=" + i + ", score=" + scoreTable[i]);
		}
	}

	public void playInit() {
		clearBoard();
	}
	
	public void humanGo(int pos) {
		board[pos] = getTurn();
		this.printBoard();
		togTurn();
	}
	
	public int aiGo() {
		// AI player
		int maxIdx = -1;
		float maxScore = -1.0f;
		for (int i = 0; i < 9; i++) {
			if (board[i] == 0) {
				board[i] = getTurn();
				int index = this.getBoardIndex();
				System.out.println("i=" + (i + 1) + ", score=" + scoreTable[index]);
				if (scoreTable[index] > maxScore) {
					maxScore = scoreTable[index];
					maxIdx = i;
				}
				board[i] = 0;
			}
		}
		if (maxIdx == -1)
			maxIdx = getNextFreePosition();
		if (maxIdx != -1) {
			board[maxIdx] = getTurn();
			this.printBoard();
			togTurn();
		}
		return maxIdx;
	}
	
	public void play() {
		clearBoard();
		printBoard();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.equalsIgnoreCase("quit")) {
					break;
				}
				System.out.println("Line entered : " + line);
				int p = Integer.parseInt(line) - 1;
				board[p] = getTurn();
				this.printBoard();
				int winer = calWiner();
				if (winer > 0) {
					System.out.println("Winer:" + formatWinner(winer));
					int index = this.getBoardIndex();
					System.out.println("score=" + scoreTable[index]);
					clearBoard();
					continue;
				}
				togTurn();

				// AI player
				int maxIdx = -1;
				float maxScore = -1.0f;
				for (int i = 0; i < 9; i++) {
					if (board[i] == 0) {
						board[i] = getTurn();
						int index = this.getBoardIndex();
						System.out.println("i=" + (i + 1) + ", score=" + scoreTable[index]);
						if (scoreTable[index] > maxScore) {
							maxScore = scoreTable[index];
							maxIdx = i;
						}
						board[i] = 0;
					}
				}
				if (maxIdx == -1)
					maxIdx = getNextFreePosition();
				if (maxIdx == -1) {
					System.out.println("K.O.");
					clearBoard();
					continue;
				}
				board[maxIdx] = getTurn();
				this.printBoard();
				winer = calWiner();
				if (winer > 0) {
					System.out.println("Winer:" + formatWinner(winer));
					clearBoard();
					continue;
				}
				togTurn();
			}
		} catch (IOException ioe) {
			System.out.println("Exception while reading input " + ioe);
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error while closing stream: " + ioe);
			}
		}
	}

	public void updateScoreMap(int index) {
		float minScore = 1.0f;
		for (int j = 0; j < 9; j++) {
			if (board[j] == 0) {
				board[j] = getTurn();
				if (this.calWiner() > 0) {
					minScore = -1.0f;
					board[j] = 0;
					break;
				}
				this.togTurn();
				float maxScore = -1.0f;
				for (int i = 0; i < 9; i++) {
					if (board[i] == 0) {
						board[i] = getTurn();
						if (this.calWiner() > 0) {
							maxScore = 1.0f;
							board[i] = 0;
							break;
						}
						float score = scoreTable[getBoardIndex()];
						if (score > maxScore) {
							maxScore = score;
						}
						board[i] = 0;
					}
				}
				if (minScore > maxScore) {
					minScore = maxScore;
				}
				this.togTurn();
				board[j] = 0;
			}
		}
		//if(minScore == -1)
		//	scoreTable[index] = -1;
		//else
			scoreTable[index] += alpha * (minScore - scoreTable[index]);
	}

	public int getBoardIndex() {
		StringBuilder sb = new StringBuilder();
		for (int i : board) {
			sb.append(i);
		}
		return Integer.parseInt(sb.toString(), 3);
	}

	public String formatWinner(int a) {
		if (a == 1) {
			return "X";
		} else if (a == 2) {
			return "O";
		} else
			return ".";
	}

	public int[][] lines = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
			{ 0, 4, 8 }, { 2, 4, 6 } };

	public int calWiner() {
		for (int i = 0; i < lines.length; i++) {
			int a = lines[i][0];
			int b = lines[i][1];
			int c = lines[i][2];
			if (board[a] != 0 && board[a] == board[b] && board[a] == board[c]) {
				return board[a];
			}
		}
		return 0;
	}

	public int getTurn() {
		int v = 2;
		if (turnX)
			v = 1;
		return v;
	}

	public void togTurn() {
		turnX = !turnX;
	}

	public int stepNext() {
		int p = getNextFreePosition();
		if (p >= 0) {
			board[p] = getTurn();
			togTurn();
		}
		return p;
	}

	public int getNextFreePosition() {
		int[] available = new int[9];
		int j = 0;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) {
				available[j++] = i;
			}
		}
		if (j > 0) {
			return available[(int) (Math.random() * j)];
		}
		return -1;
	}

	public void clearBoard() {
		for (int i = 0; i < board.length; i++)
			board[i] = 0;
		turnX = true;
	}

	public void printBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				System.out.print(formatWinner(board[j + 3 * i]) + " ");
			}
			System.out.println();
		}
	}
}
