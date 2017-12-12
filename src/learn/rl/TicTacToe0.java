package learn.rl;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStreamReader;

//This is not working due to some bugs
public class TicTacToe0 {
	float[][] scoreMap = new float[19683][9];
	int[] board = new int[9];
	public boolean turnX = true;
	float alpha = 0.01f;

	public static void main(String[] args) {
		TicTacToe0 t = new TicTacToe0();
		
		for(int i=0; i<90000; i++) {
			t.clearBoard();
			int pos = -1;
			do {
				pos = t.stepNext();
				int index = t.getBoardIndex();
				int winer = t.calWiner();
				if(winer > 0) {
					//System.out.println("Winer:"+t.formatWinner(winer));
					for(int j=0; j<9; j++) {
						t.scoreMap[index][j] = (winer==2)?1.0f:-1.0f;
					}
					//t.updateScoreMap(index);
					break;
				}
				t.updateScoreMap(index);
			} while(pos > 0);
		}
		t.printScoreMap();
		t.play();
	}
	
	public void printScoreMap() {
		for(float[] r : scoreMap) {
			float sum = 0.0f;
			for(float s : r) {
				sum += s;
			}
			if(sum <0) {
				for(float s : r) {
					System.out.print(s+" ");
				}
				System.out.println();
			}
		}
	}
	
	public void play() {
		clearBoard();
		BufferedReader br = null;
        try {
        	br = new BufferedReader(new InputStreamReader(System.in));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase("quit")) {
                    break;
                }
                System.out.println("Line entered : " + line);
                int p = Integer.parseInt(line)-1;
    			board[p] = getTurn();
                this.printBoard();
				int winer = calWiner();
				if(winer > 0) {
					System.out.println("Winer:"+formatWinner(winer));
					clearBoard();
					continue;
				}
    			togTurn();
    			int index = this.getBoardIndex();
    			int maxIdx = -1;
    			float maxScore = -1.0f;
    			printScoreMap(scoreMap[index]);
    			for(int i=0; i<9; i++) {
    				if(board[i] ==0 && scoreMap[index][i] > maxScore) {
    					maxScore = scoreMap[index][i];
    					maxIdx = i;
    				}
    			}
    			if(maxIdx == -1)
    				maxIdx = getNextFreePosition();
    			board[maxIdx] = getTurn();
                this.printBoard();
    			winer = calWiner();
				if(winer > 0) {
					System.out.println("Winer:"+formatWinner(winer));
					clearBoard();
					continue;
				}
    			togTurn();
            }
        }
        catch (IOException ioe) {
            System.out.println("Exception while reading input " + ioe);
        }
        finally {
            try {
                if (br != null) {
                    br.close();
                }
            }
            catch (IOException ioe) {
                System.out.println("Error while closing stream: " + ioe);
            }
        }
	}
	
	public float getMaxScore(int idx) {
		int winer = calWiner();
		if(winer > 0) {
			if(winer == 1) return -1.0f;
			else return 1.0f;
		}
		
		float max = -1.0f;
		String s = String.format("%09d",Integer.parseInt(Integer.toString(idx, 3)));
		for(int i=0; i<9; i++) {
			if(s.charAt(i) == '0' && scoreMap[idx][i] > max)
				max = scoreMap[idx][i];
		}
		return max;
	}
	
	//need to consider opponent find min ( max (score) )
	public void updateScoreMap(int index) {
		for(int j=0; j<9; j++) {
			if(board[j] == 0) {
				board[j] = getTurn();
				this.togTurn();
				for(int i=0; i<9; i++) {
					if(board[i] == 0) {
						board[i] = getTurn();
						float maxScore = -1.0f;
						float score = getMaxScore(getBoardIndex());
						if(score > maxScore)
							maxScore = score;
						scoreMap[index][i] += alpha*(maxScore - scoreMap[index][i]);
						board[i] = 0;
					}
				}
				this.togTurn();
				board[j] = 0;
			}
		}
	}
	
	public int getBoardIndex() {
		StringBuilder sb = new StringBuilder();
		for(int i : board) {
			sb.append(i);
		}
		return Integer.parseInt(sb.toString(), 3);
	}

	public String formatWinner(int a) {
		if(a == 1) {
			return "X";
		} else if(a == 2) {
			return "O";
		} else
			return ".";
	}

	public int[][] lines = new int[][] { 
		{ 0, 1, 2 },
		{ 3, 4, 5 },
		{ 6, 7, 8 },
		{ 0, 3, 6 },
		{ 1, 4, 7 },
		{ 2, 5, 8 },
		{ 0, 4, 8 },
		{ 2, 4, 6 }
	};

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
		if (p > 0) {//bugfix p>=0
			board[p] = getTurn();
			togTurn();
		}
		return p;
	}

	// todo random
	public int getNextFreePosition() {
		int[] available = new int[9];
		int j=0;
		for (int i = 0; i < board.length; i++) {
			if (board[i] == 0) {
				available[j++] = i;
			}
		}
		if(j > 0) {
			return available[(int)(Math.random()*j)];
		}
		return -1;
	}

	public void clearBoard() {
		for (int i = 0; i < board.length; i++)
			board[i] = 0;
	}
	
	public void printBoard() {
		for(int i=0; i<3; i++) {
			for(int j=0; j<3; j++) {
				System.out.print(formatWinner(board[j+3*i])+" ");
			}
			System.out.println();
		}
	}
	
	public void printScoreMap(float[] row) {
		for(float f : row) {
			System.out.print(f+" ");
		}
		System.out.println();
	}

}
