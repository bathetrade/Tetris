package logic;

import java.util.Random;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import animation.RowDeleteAnimation;

import pieces.ActivePiece;
import pieces.GameBoardSquare;
import point.*;

/**
 * GameBoard represents the playing area. Used for collision detection, moving pieces, deleting rows, etc.
 *
 */
public class GameBoard {
	
	private int rows;
	private int cols;
	
	private boolean animateRowDeleteFlag = false;

	private GameBoardSquare[][] gameBoard;  //Gameboard in "logic space," i.e., a matrix of booleans
	private RowDeleteAnimation animateRowDelete;
	private ActivePiece activePiece;   
	private Random RNG = null;
	private long randomNum;
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
		
		this.rows      = Math.abs(rows);
		this.cols      = Math.abs(cols);
		gameBoard      = new GameBoardSquare[this.rows][this.cols];
		
		//Create each GameBoardSquare on the game board
		for (int i = 0; i < this.rows; ++i) {
			for (int j = 0; j < this.cols; ++j)
				gameBoard[i][j] = new GameBoardSquare();
		}
		
		activePiece           = new ActivePiece(this);
		RNG 		          = new Random();
		randomNum             = RNG.nextLong();
		
		//Animation stuff
		animateRowDelete      = new RowDeleteAnimation(this);
		animateRowDeleteFlag  = false;
		
	}
	
	
	
	/**
	 * Checks the board for full rows and sets the animateRowDelete flag to true if there are. This
	 * method should be called each time a piece lands.
	 */
	public void update() {
		if (animateRowDeleteFlag == false)
			animateRowDeleteFlag = checkFullRows();
	}
	
	
	
	
	/**
	 * This method checks to see whether there are any full rows on the board that need to be
	 * cleared. It checks from the bottom up for efficiency, and returns as soon as it finds a full
	 * row (i.e., it does not check the entire board).
	 * @return Returns true if there are rows to be deleted, and false otherwise.
	 */
	public boolean checkFullRows() {
		boolean fullRow;
		int maxRowIndex = this.rows - 1;
		int maxColIndex = this.cols - 1;
		
		for (int i = maxRowIndex; i >= 0; --i) {
			fullRow = true;
			for (int j = maxColIndex; j >= 0; --j) {
				if (!isSet(i,j)) {
					fullRow = false;
					break;
				}
			}
			if (fullRow)
				return true;
		}
		return false;
	}
	
	
	
	
	public int getRows() {
		return this.rows;
	}
	
	
	
	
	public int getCols() {
		return this.cols;
	}

	
	
	/**
	 * Checks whether an array of subsquares has at least one of them set. Basically, the same as isSet(int,int)
	 * except for an array.
	 * @param piece - An array of subsquares.
	 * @return Returns true if at least one element of the array is set on the game board. Otherwise, false.
	 * Does not count a subsquare being out of bounds as a collision.
	 */
	public boolean checkPieceCollision(Point[] piece) {
		for (int i = 0; i < 4; ++i) {
				if (isSet(piece[i]))
					return true;
		}
		return false;
	}
	
	
	
	
	public boolean isPieceInBounds(Point[] piece) {
		for (int i = 0; i < 4; ++i) {
			if (!inBounds(piece[i]))
				return false;
		}
		return true;
	}
	
	
	
	
	public boolean outOfBoundsLeft(int row, int col) {
		return col < 0;
	}
	
	
	
	
	public boolean outOfBoundsLeft(Point p) {
		return p.y < 0;
	}
	
	
	
	
	public boolean outOfBoundsRight(Point p) {
		return p.y >= this.cols;
	}
	
	
	
	
	public boolean outOfBoundsBottom(Point p) {
		return p.x >= this.rows;
	}
	
	
	
	
	public boolean outOfBoundsRight(int row, int col) {
		return col >= this.cols;
	}
	
	
	
	
	public boolean outOfBoundsBottom(int row, int col) {
		return row >= this.rows;
	}
	
	
	
	
	public boolean inBounds(int row, int col) {
		if (row < 0 || row >= this.rows)
			return false;
		if (col < 0 || col >= this.cols)
			return false;
		return true;
	}
	
	
	
	
	public boolean inBounds(Point p) {
		return inBounds(p.x, p.y);
	}
	
	
	
	
	public boolean inBoundsLeftRightBottom(int row, int col) {
		return col >= 0 && col < this.cols && row < this.rows;
	}
	
	
	
	
	public boolean inBoundsLeftRightBottom(Point p) {
		return inBoundsLeftRightBottom(p.x, p.y);
	}
	
	
	
	
	public boolean isAnimationPlaying() {
		return animateRowDeleteFlag;
	}
	
	
	
	
	public boolean isPieceInBoundsLeftRightBottom(Point[] p) {
		for (int i = 0; i < 4; ++i) {
			if (!inBoundsLeftRightBottom(p[i].x, p[i].y))
				return false;
		}
		return true;
	}
	
	
	
	
	public boolean isSet(int row, int col) {
		return !inBounds(row,col) ? false : gameBoard[row][col].isSet();
	}
	
	
	
	
	public boolean isSet(Point p) {
		return !inBounds(p.x, p.y) ? false : gameBoard[p.x][p.y].isSet();
	}
	
	
	
	
	public ActivePiece getActivePiece() {
		return activePiece;
	}
	
	
	
	
	public GameBoardSquare getSquare(int row, int col) {
		return inBounds(row,col) ? gameBoard[row][col] : null;
	}
	
	
	
	public GameBoardSquare getSquare(Point p) {
		return inBounds(p.x, p.y) ? gameBoard[p.x][p.y] : null; 
	}
	
	
	
	
	public void printBoard() {
		int set;
		for (int i=0; i<rows; ++i) {
			for (int j=0; j<cols; ++j) {
				set       = gameBoard[i][j].isSet() ? 1 : 0;
				System.out.print(set + "  ");
			}
			System.out.println();
		}
		System.out.println("");
		System.out.println("");
	}
	
	
	
	
	public void startAnimation() {
		this.animateRowDeleteFlag = true;
	}
	
	
	
	
	public boolean setSquare(int row, int col, Color color) {
		return !inBounds(row,col) ? false : gameBoard[row][col].setSquare(color);
	}
	
	
	
	public boolean setSquare(Point p, Color color) {
		return !inBounds(p.x, p.y) ? false : gameBoard[p.x][p.y].setSquare(color);
	}
	
	
	
	
	public void clearRow(int row) {
		for (int col = 0; col < this.cols; ++col) {
			clearSquare(row, col);
		}
	}
	
	
	
	
	public void clearSquare(int row, int col) {
		if (inBounds(row,col))
			gameBoard[row][col].clearSquare();
	}
	
	
	
	
	public void clearSquare(Point p) {
		clearSquare(p.x, p.y);
	}
	
	
	
	
	public boolean spawnPiece() {
		int hw = cols/2;  //Half of the board's width.
		
		//PieceType type = PieceType.PIECE_LINE;
		PieceType type = PieceType.fromInteger(RNG.nextInt(PieceType.numPieces));
		
		randomNum ^= randomNum << 22;
		randomNum ^= randomNum >>> 35;
		randomNum ^= randomNum << 4;
		System.out.println(randomNum);
		if (randomNum < 0)
			randomNum *= -1;
		type = PieceType.fromInteger((int)(randomNum % PieceType.numPieces));
		
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
			Point[] SPiece =      {new Point(0,hw-1), new Point(0,hw),
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
	
	
	
	
	public void render(GameContainer container, Graphics g) {
		
		
		
		//Render board normally (using logic space to render into screen space)
		
		if (!isAnimationPlaying()) {
			for (int i=0; i<rows; ++i) {
				for (int j=0; j<cols; ++j)
					gameBoard[i][j].render(g, i, j);
			}
		}
		
		//If it's time to clear the rows, then render the board specially.
		else 
			animateRowDeleteFlag = animateRowDelete.play(g);
		
	}
	
	
	
}
