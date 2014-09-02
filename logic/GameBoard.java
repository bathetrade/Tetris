package logic;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

import pieces.ActivePiece;
import pieces.GameBoardSquare;
import point.Point;
import point.Vec2D;
import tetrisgame.TetrisGame;

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
	
	
	
	
	private Vec2D boardToScreenOriginVector(GameContainer container) {
		Point screenCenter = new Point(container.getWidth()/2, container.getHeight()/2);
		int hbw            = (TetrisGame.pieceSize*TetrisGame.blockWidth)/2;
		int hbh            = (TetrisGame.pieceSize*TetrisGame.blockHeight)/2;
		Point topLeftBoard = new Point(screenCenter.x - hbw, screenCenter.y - hbh);
		
		//Get the vector that translates the origin of the visible game area to the origin of the
		//	on-screen playing area.
		//For instance, if there are two invisible rows, and we want the game area to have a blockHeight of
		//	10 (a total of 12 rows), then the 0th row and the 1st row are invisible. The rows 2 through 11 are visible. So, we
		//	want to map (2,0) (2nd row, 0th column) to the on-screen playing area's origin (i.e., topLeftBoard).
		Vec2D originOffset = new Vec2D(topLeftBoard.x, topLeftBoard.y - TetrisGame.numInvisRows * TetrisGame.pieceSize);
		return originOffset;
	}
	
	
	
	
	public void update() {
		
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
	}
	
	
	
	public boolean setSquare(int row, int col, Color color) {
		return !inBounds(row,col) ? false : gameBoard[row][col].setSquare(color);
	}
	
	
	
	public boolean setSquare(Point p, Color color) {
		return !inBounds(p.x, p.y) ? false : gameBoard[p.x][p.y].setSquare(color);
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
	public void clearRows(GameContainer container) {
		
		//check the whole board for completed rows
		//Store the row indices in a 4-dimensional array (we can only have 
		//	a maximum of 4 rows deleted at once.)
		List<Integer> deletedRows = new ArrayList<Integer>(4);
		for (int i = 0; i < rows; ++i) {
			boolean fullRow = true;
			for (int j = 0; j < cols; ++j) {
				if (!getSquare(i,j).isSet())
					fullRow = false;
			}
			//If fullRow = true, then we add the row to be deleted.
			if (fullRow == true)
				deletedRows.add(i);
		}
		
		if (deletedRows.isEmpty())
			return;
		
		//Now, we animate the row deletion.
		
		//For now, just do a blocking animation instead of a non-blocking one for simplicity.
		/* General animation algorithm:
		 * 
		 * 1) Erase the empty rows from the screen and clear them from the board.
		 * 
		 * 2) Partition the board into chunks separated by empty rows. Clear all the chunks off the board (but not the screen)
		 * except for the bottom chunk.
		 * 
		 * 3) Get the bounding rectangle for the bottom chunk in screen space coordinates.
		 * 
		 * Each frame of animation:
		 * 4) Apply the position function s(t) = 1/2at^2 + v_0*t + s_0 to each chunk in screen space, where t
		 * 	  is the time since the beginning of the last animation frame, and v_0 = 0.
		 *	 (Won't work in logic space because it uses ints only.)
		 * 
		 * 5) Keep doing this until the bottom chunk is in the bounding rectangle. Make sure to realign the
		 *	  chunk to account for floating point imprecision, and set the chunk on the board again.
		 *
		 * 6) Update the bounding rectangle to include the new chunk, and set the "chunk pointer" to the
		 * 	  next-highest chunk. Repeat until all the chunks are animated.
		 */
		
		//Clear empty rows from the board.
		for (Integer rowIndex : deletedRows) {
			System.out.println("Row index: " + rowIndex.intValue());
			for (int colIndex = 0; colIndex < this.cols; ++colIndex)
				clearSquare(rowIndex.intValue(), colIndex);
		}
		
		//Get the "collision line" that falling chunks cannot go below. This is the top line of the bounding rectangles
		//(we don't actually need bounding rectangles, only the top line of them). The falling chunk that is lowest to the floor will hit this
		//collision line, and then we update the collision line to be the top of that chunk which has just landed. The other falling chunks will hit
		//this line, and so on, until all the chunks have fallen.
		
		int collisionLine = 0; // this is the y component in screen space
		Vec2D originOffset = boardToScreenOriginVector(container);
		for (Integer rowIndex : deletedRows) {
			int colLineBoardIndex = rowIndex.intValue() + 1;
			collisionLine = GameBoardSquare.boardToScreen(colLineBoardIndex, 0, originOffset).y;
		}
	}
	
	
	
	public void render(GameContainer container, Graphics g) {
		
		Vec2D boardToScreenOffset = boardToScreenOriginVector(container);
		for (int i=0; i<rows; ++i) {
			for (int j=0; j<cols; ++j)
				gameBoard[i][j].render(g, i, j, boardToScreenOffset);
		}
	}
	
	
	
}
