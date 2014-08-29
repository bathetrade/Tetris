package logic;

import java.util.Random;
import java.util.Collection;
import java.util.ArrayList;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import pieces.ActivePiece;
import pieces.GameBoardSquare;
import point.Point;

/**
 * GameBoard represents the playing area. Used for collision detection, moving pieces, deleting rows, etc.
 * @author Derek
 *
 */
public class GameBoard {
	
	private int rows;
	private int cols;
	private GameBoardSquare[][] gameBoard;  //GameBoard :)
	private ActivePiece activePiece;   
	private Random RNG = null;
	public enum PieceType {

		PIECE_L,
		PIECE_J,
		PIECE_S,
		PIECE_Z,
		PIECE_T,
		PIECE_SQUARE,
		PIECE_LINE;
		
		public static final int numPieces = 7;
		
		public static PieceType fromInteger(int x) {
			switch (x) {
			case 0:
				return PIECE_L;
			case 1:
				return PIECE_J;
			case 2:
				return PIECE_S;
			case 3:
				return PIECE_Z;
			case 4:
				return PIECE_T;
			case 5:
				return PIECE_SQUARE;
			case 6:
				return PIECE_LINE;
			}
			return null;
		}
	}

	
	
	public GameBoard(int rows, int cols) {
		
		this.rows = Math.abs(rows);
		this.cols = Math.abs(cols);
		gameBoard = new GameBoardSquare[this.rows][this.cols];
		
		//Create each GameBoardSquare on the game board
		for (int i = 0; i < this.rows; ++i) {
			for (int j = 0; j < this.cols; ++j)
				gameBoard[i][j] = new GameBoardSquare();
		}
		activePiece = new ActivePiece(this);
		RNG 		= new Random();
	}
	
	
	
	public void update() {
		
	}
	
	
	
	public int getRows() {
		return this.rows;
	}
	
	
	
	public int getCols() {
		return this.cols;
	}

	
	
	public boolean inBounds(int row, int col) {
		if (row < 0 || row >= this.rows)
			return false;
		if (col < 0 || col >= this.cols)
			return false;
		return true;
	}
	
	
	
	public boolean isSet(int row, int col) {
		if (!inBounds(row,col))
			return false;
		else return gameBoard[row][col].isOccupied();
	}
	
	
	
	public ActivePiece getActivePiece() {
		return activePiece;
	}
	
	
	
	public GameBoardSquare getSquare(int row, int col) {
		return (inBounds(row,col) ? gameBoard[row][col] : null);
	}
	
	
	
	public void printBoard() {
		int set;
		for (int i=0; i<rows; ++i) {
			for (int j=0; j<cols; ++j) {
				set       = gameBoard[i][j].isOccupied() ? 1 : 0;
				System.out.print(set + "  ");
			}
			System.out.println();
		}
	}
	
	
	
	public boolean setSquare(int row, int col, Color color) {
		return (!inBounds(row,col) ? false : gameBoard[row][col].setSquare(color));
	}
	
	
	
	public void unsetSquare(int row, int col) {
		if (inBounds(row,col))
			gameBoard[row][col].unsetSquare();
	}
	
	
	
	public boolean spawnPiece() {
		int hw = cols/2;  //Half of the board's width.
		
		PieceType type = PieceType.fromInteger(RNG.nextInt(PieceType.numPieces));
		
		//Pieces are specified in logic space. Example of a line:
		//0 0 0 0 0 0 ....
		//0 1 1 1 1 0 ....
		//0 0 0 0 0 0 ....
		//This logic space is treated as a matrix, such that (0, 0) is at the top left.
		//The first point for each piece is the point that the piece rotates about.
		
		switch(type) {
		
		case PIECE_L:
			Point[] LPiece =      {new Point(1, hw),  new Point(0,hw),
							       new Point(2, hw),  new Point(2, hw+1)};
			return activePiece.setPiece(LPiece, type);
			
		case PIECE_J:
			Point[] JPiece =      {new Point(1,hw),   new Point(0,hw),
							       new Point(2,hw),   new Point(2,hw-1)};
			return activePiece.setPiece(JPiece, type);
			
		case PIECE_S:
			Point[] SPiece =      {new Point(0,hw-1),   new Point(0,hw),
							       new Point(1,hw-1), new Point(1,hw-2)};
			return activePiece.setPiece(SPiece, type);
			
		case PIECE_Z:
			Point[] ZPiece =      {new Point(0,hw-1), new Point(0,hw-2),
							       new Point(1,hw-1), new Point(1,hw)};
			return activePiece.setPiece(ZPiece, type);
			
		case PIECE_T:
			Point[] TPiece =      {new Point(1,hw),   new Point(0,hw),
							       new Point(1,hw-1), new Point(1,hw+1)};
			return activePiece.setPiece(TPiece, type);
			
		case PIECE_SQUARE:
			Point[] SquarePiece = {new Point(0,hw-1), new Point(0,hw),
								   new Point(1,hw-1), new Point(1,hw)};
			return activePiece.setPiece(SquarePiece, type);
			
		case PIECE_LINE:
			Point[] LinePiece =   {new Point(1,hw),   new Point(0,hw),
								   new Point(2,hw),   new Point(3,hw)};
			return activePiece.setPiece(LinePiece, type);
		}
		
		return true;
	}
	
	
	
	//Finish this method
	public int clearRows() {
		
		//check the whole board for completed rows
		//Store the row indices in a 4-dimensional array (we can only have 
		//	a maximum of 4 rows deleted at once.)
		Collection<Integer> deletedRows = new ArrayList<Integer>(4);
		for (int i = 0; i < rows; ++i) {
			boolean fullRow = true;
			for (int j = 0; j < cols; ++j) {
				if (!getSquare(i,j).isOccupied())
					fullRow = false;
			}
			//If fullRow = true, then we add the row to be deleted.
			if (fullRow == true)
				deletedRows.add(i);
		}
		
		//Now, we animate the row deletion.
		
		return 0;
	}
	
	
	
	public void render(GameContainer container, Graphics g) {
		for (int i=0; i<rows; ++i) {
			for (int j=0; j<cols; ++j)
				gameBoard[i][j].render(container, g, i, j);
		}
	}
	
	
	
}
